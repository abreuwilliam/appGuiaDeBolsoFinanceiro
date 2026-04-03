package com.example.guiaFinanceiro.service;

import com.example.guiaFinanceiro.entites.FinancialGoal;
import com.example.guiaFinanceiro.repository.FinancialGoalRepository;
import com.example.guiaFinanceiro.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IaService {

    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WebClientIaService webClientIaService;

    @Autowired
    private AccountService accountService;

    public Map<String, String> getAdvice(UUID financialGoalId) {
        System.out.println("Chamando IA para meta: " + financialGoalId);

        FinancialGoal goal = financialGoalRepository
                .findById(financialGoalId)
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada"));

        // Cálculos auxiliares para enriquecer o prompt
        BigDecimal progressoPercentual = goal.getCurrentAmount()
                .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        BigDecimal falta = goal.getTargetAmount().subtract(goal.getCurrentAmount());

        String prompt = String.format(
                """
                # PERSONA
                Você é um Mentor Financeiro focado em Psicologia Econômica e Metas. 
                Seu tom é motivador, mas baseado na realidade dos números.
    
                # DADOS DA META
                - NOME: "%s"
                - VALOR ALVO: R$ %s
                - JÁ CONQUISTADO: R$ %s (%s%% concluído)
                - FALTA ECONOMIZAR: R$ %s
                - DATA LIMITE: %s
    
                # TAREFA
                Com base no progresso de %s%%, gere uma instrução curta (máximo 3 frases).
                - Se o progresso for < 20%%: Dê um empurrão inicial focado em constância.
                - Se o progresso for > 80%%: Use um tom de 'reta final' e celebração antecipada.
                - Caso contrário: Dê uma dica prática de economia específica para quem quer atingir "%s".
    
                # REGRAS
                - Não diga "Olá". Vá direto ao ponto.
                - Use 1 emoji que combine com a meta.
                - Responda em Português do Brasil.
                """,
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                progressoPercentual.setScale(0, RoundingMode.HALF_UP),
                falta,
                goal.getTargetDate(),
                progressoPercentual.setScale(0, RoundingMode.HALF_UP),
                goal.getName()
        );

        String advice = webClientIaService.getFinancialAdvice(prompt);

        return Map.of("advice", advice);
    }

        public String analyzeUserFinances(UUID userId) {
            // 1. COLETA DE DADOS - USANDO OS MÉTODOS REAIS DO SEU REPOSITORY
            BigDecimal renda = transactionRepository.getRendaMensalPorUsuario(userId);

            // Mapeado para sumGastoContaByUserId (Lógica do App: Gasto Efetivo da Conta)
            BigDecimal gastoConta = transactionRepository.sumGastoContaByUserId(userId);

            // Mapeado para findTotalDebitsByUserId (Gasto Total: Conta + Cartão)
            BigDecimal gastosTotais = transactionRepository.findTotalDebitsByUserId(userId);

            BigDecimal variacao = transactionRepository.getVariacaoGastoMensal(userId);
            BigDecimal gastoDiario = transactionRepository.getMediaGastoDiario(userId);
            BigDecimal usoCartao = transactionRepository.getPercentualUsoCartao(userId);

            // 2. CÁLCULO DO SCORE (SINCRONIZADO COM O FRONT-END)
            // Lógica: (Gasto Conta / Renda) * 100
            BigDecimal healthScoreCorrigido = BigDecimal.ZERO;
            if (renda != null && renda.compareTo(BigDecimal.ZERO) > 0) {
                healthScoreCorrigido = gastoConta
                        .divide(renda, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            BigDecimal saldoEfetivo = accountService.findBalanceAccount(userId);
            List<Object[]> gastosCategoria = transactionRepository.findGastoPorCategoriaGrouped(userId);

            StringBuilder categoriasStr = new StringBuilder();
            for (Object[] row : gastosCategoria) {
                categoriasStr.append("- ").append(row[0]).append(": R$ ").append(row[1]).append("\n");
            }

            // 3. O PROMPT DE ALTA PERFORMANCE
            String contexto = """
            # PERSONA
            Você é um Analista Financeiro Sênior. Sua linguagem é técnica, direta e focada em otimização de fluxo de caixa.

            # METODOLOGIA
            O App prioriza o 'Gasto Efetivo' (Saída real da conta). 
            - Score de Comprometimento: %s%% (Gasto Conta / Renda).
            - Referência: Abaixo de 35%% é excelente. Acima de 70%% é crítico.

            # DASHBOARD DE DADOS (VALORES REAIS)
            - Renda Identificada: R$ %s
            - Gasto na Conta (Débito/Pix): R$ %s
            - Saldo Líquido Atual: R$ %s
            - Uso do Cartão de Crédito: %s%% do volume mensal
            - Variação Mensal de Gastos: %s%%
            - Custo de Vida Diário: R$ %s

            # COMPOSIÇÃO POR CATEGORIA
            %s

            # TAREFA PARA A IA
            1. **Diagnóstico**: Avalie o comprometimento de %s%%. Se a variação de %s%% for positiva, analise se o aumento foi em categorias essenciais ou supérfluas.
            2. **Gargalo**: Identifique a categoria que mais consome saldo e sugira um corte de 10%%.
            3. **Instrução Imediata**: O que o usuário deve fazer hoje com os R$ %s que sobraram para não fechar o mês no vermelho?

            # REGRAS
            - Seja conciso (máximo 150 palavras).
            - Use emojis para destacar insights.
            - Responda em Português do Brasil.
            """.formatted(
                    healthScoreCorrigido.setScale(0, RoundingMode.HALF_UP),
                    renda, gastoConta, saldoEfetivo,
                    usoCartao, variacao, gastoDiario,
                    categoriasStr.toString(),
                    healthScoreCorrigido.setScale(0, RoundingMode.HALF_UP),
                    variacao, saldoEfetivo
            );

            return webClientIaService.getFinancialAdvice(contexto);
        }
    }
