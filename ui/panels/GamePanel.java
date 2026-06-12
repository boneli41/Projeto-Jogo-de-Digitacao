package ui.panels; // Define o pacote onde esta classe reside

import factory.ExerciseFactory; // Importa a fábrica que cria os exercícios
import model.Exercise; // Importa a classe base de exercícios
import model.Player; // Importa a classe que guarda os dados do jogador
import ui.TypingGame; // Importa a janela principal do jogo

import javax.swing.*; // Importa componentes gráficos básicos (JLabel, JPanel, etc)
import javax.swing.text.*; // Importa classes para manipular texto estilizado (cores no texto)
import java.awt.*; // Importa classes de layout e cores básicas
import java.awt.event.*; // Importa classes para capturar eventos de teclado e mouse
import java.util.List; // Importa a interface de Lista do Java

public class GamePanel extends BasePanel { // Define a classe que herda as cores e fontes de BasePanel

    // -------- Estado do Modelo (Lógica de dados) --------
    private Player player; // Referência para o jogador atual
    private List<Exercise> exercises; // Lista com todos os exercícios do jogo
    private int exerciseIndex; // Índice de qual exercício está sendo feito agora
    private Exercise current; // O objeto do exercício atual
    private boolean active; // Indica se o jogo está rodando ou pausado/finalizado
    private long startTime; // Guarda o momento exato em que o exercício começou
    private int timeLeft; // Contador de segundos restantes

    // -------- Rótulos da barra superior --------
    private JLabel lblPlayerName; // Exibe o nome do idoso
    private JLabel lblLevel; // Exibe o nível (Iniciante, Aprendiz, etc)
    private JLabel lblLives; // Exibe os corações (vidas)
    private JLabel lblScore; // Exibe a pontuação total
    private JLabel lblTimer; // Exibe o cronômetro

    // -------- Rótulos da barra lateral --------
    private JLabel lblSideXP;     // XP total na lateral
    private JLabel lblSideLevel;  // Nível atual na lateral
    private JLabel lblSideStreak; // Sequência na lateral
    private JLabel lblNextLevelHint; // "Faltam X questões..."

    // -------- Área do exercício (Centro) --------
    private JLabel lblCategory; // Exibe a categoria do exercício
    private JLabel lblInstructions; // Exibe a instrução (ex: "Digite a frase abaixo")
    private JTextPane txtExercise; // Área que mostra o texto colorido (onde o usuário lê)
    private JTextField txtInput; // Campo onde o usuário realmente digita
    private JLabel lblFeedback; // Mensagem de incentivo ou erro

    // -------- Barra inferior --------
    private JProgressBar progressBar; // Barra de progresso do exercício atual
    private JLabel lblProgress; // Texto indicando qual exercício de quantos (ex: 1 de 40)

    // -------- Temporizador --------
    private Timer countdown; // Objeto que faz a contagem regressiva

    // -------- Cores para o feedback de caracteres --------
    private static final Color COL_CORRECT_FG = new Color( 27, 135,  50); // Verde escuro para letra correta
    private static final Color COL_CORRECT_BG = new Color(220, 255, 220); // Verde claro para o fundo da letra correta
    private static final Color COL_WRONG_FG   = new Color(180,  20,  20); // Vermelho para letra errada
    private static final Color COL_WRONG_BG   = new Color(255, 220, 220); // Rosa claro para o fundo da letra errada
    private static final Color COL_CURSOR_BG  = new Color(180, 220, 255); // Azul claro para destacar onde o usuário deve digitar
    private static final Color COL_NORMAL_FG  = new Color( 60,  60,  60); // Cinza escuro para letras ainda não digitadas

    public GamePanel(TypingGame game) { // Construtor da classe
        super(game); // Chama o construtor da BasePanel
    }

    @Override
    protected void initialize() { // Método que organiza os componentes na tela
        setLayout(new BorderLayout());
        setOpaque(false);

        // Estrutura principal da página com imagem de fundo
        JPanel content = new JPanel(new BorderLayout()) {
            private final Image bgImage = new ImageIcon("ui/assets/background-image-gamepanel.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        add(content, BorderLayout.CENTER);

        content.add(buildTopBar(),    BorderLayout.NORTH);
        content.add(buildSidebar(),   BorderLayout.EAST);
        content.add(buildCenter(),    BorderLayout.CENTER);
        content.add(createFooterProgress(), BorderLayout.SOUTH);
    }

    // Barra Superior com Gradiente
    private JPanel buildTopBar() { // Cria o painel de status do topo
        GradientPanel bar = new GradientPanel(COLOR_PRIMARY, COLOR_PRIMARY, true);
        bar.setPreferredSize(new Dimension(0, 100));
        bar.setLayout(new BorderLayout(25, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 40)); // Margens laterais aumentadas para 40

        // Esquerda: nome (linha 1) + nível (linha 2)
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 1)); // Grade com 2 linhas e 1 coluna
        left.setOpaque(false); // Deixa o fundo transparente para aparecer a cor da 'bar'
        lblPlayerName = label("Jogador", FONT_INFO, Color.WHITE, SwingConstants.LEFT); // Cria label do nome
        lblLevel      = label("Nível 1 — Iniciante", // Cria label do nível
                FONT_SMALL, new Color(200, 225, 255), SwingConstants.LEFT);
        left.add(lblPlayerName); // Adiciona nome ao painel esquerdo
        left.add(lblLevel); // Adiciona nível ao painel esquerdo

        // Centro: vidas
        lblLives = label("\u2764 \u2764 \u2764", FONT_HEARTS, new Color(255, 110, 110), SwingConstants.CENTER); // Cria corações

        // Direita: pontos (linha 1) + tempo (linha 2)
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 1)); // Grade com 2 linhas
        right.setOpaque(false); // Fundo transparente
        lblScore = label("0 pts", FONT_INFO, COLOR_GOLD, SwingConstants.RIGHT); // Label da pontuação
        lblTimer = label("--s",   FONT_TIMER_BIG, Color.WHITE, SwingConstants.RIGHT); // Timer com destaque
        right.add(lblScore); // Adiciona pontuação à direita
        right.add(lblTimer); // Adiciona timer à direita

        bar.add(left,     BorderLayout.WEST); // Fixa as informações do jogador na esquerda
        bar.add(lblLives, BorderLayout.CENTER); // Fixa as vidas no centro
        bar.add(right,    BorderLayout.EAST); // Fixa o placar e tempo na direita
        return bar; // Retorna o painel completo
    }

    // Barra Lateral com RoundedPanels
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS)); // Empilhamento vertical
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(250, 0)); // Aumentado para acomodar a margem maior
        side.setBorder(BorderFactory.createEmptyBorder(16, 15, 25, 40)); // Margem inferior aumentada

        JLabel title = label("SEU STATUS", FONT_SMALL, COLOR_SECONDARY, SwingConstants.LEFT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        side.add(title);
        side.add(Box.createVerticalStrut(20));
        side.add(sidebarCard("Pontos XP", lblSideXP = label("0", FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(15));
        side.add(sidebarCard("Sequência", lblSideStreak = label("0x", FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));
        side.add(Box.createVerticalStrut(15));
        side.add(sidebarCard("Nível Atual", lblSideLevel = label("1", FONT_SUBTITLE, Color.WHITE, SwingConstants.CENTER), COLOR_ACCENT));

        side.add(Box.createVerticalStrut(20));
        lblNextLevelHint = label("Faltam 10 para Nível 2", FONT_SMALL, COLOR_PRIMARY, SwingConstants.CENTER);
        side.add(lblNextLevelHint);

        return side;
    }

    private JPanel sidebarCard(String title, JLabel valueLabel, Color bg) {
        RoundedPanel card = new RoundedPanel(25, bg);
        card.setLayout(new BorderLayout(0, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(label(title, FONT_SMALL, Color.WHITE, SwingConstants.CENTER), BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ================================================================
    //  Área Central (Área de Jogo)
    // ================================================================
    private JPanel buildCenter() { // Cria a área central onde a mágica acontece
        JPanel center = new JPanel(new BorderLayout(0, 10)); // Painel central com bordas
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(16, 40, 25, 25)); // Margem inferior aumentada

        // Categoria + instruções
        JPanel infoRow = new JPanel(new GridLayout(2, 1, 0, 4)); // Grade para textos informativos
        infoRow.setOpaque(false); // Transparente
        lblCategory     = label("Categoria", FONT_INFO, COLOR_PRIMARY, SwingConstants.CENTER); // Categoria do exercício
        lblInstructions = label("Instruções", FONT_SMALL, new Color(80, 80, 80), SwingConstants.CENTER); // O que fazer
        infoRow.add(lblCategory); // Adiciona categoria
        infoRow.add(lblInstructions); // Adiciona instruções
        center.add(infoRow, BorderLayout.NORTH); // Coloca as informações no topo do centro

        // Cartão de exibição do exercício
        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        txtExercise = new JTextPane(); // Componente de texto que permite estilos diferentes
        txtExercise.setEditable(false); // Usuário não pode clicar e apagar o texto base
        txtExercise.setFont(FONT_EXERCISE);
        txtExercise.setBackground(COLOR_WHITE); // Fundo branco
        txtExercise.setFocusable(false); // O foco do teclado nunca deve ficar aqui
        txtExercise.setPreferredSize(new Dimension(0, 130)); // Altura fixa para o texto

        lblFeedback = label(" ", new Font("Nunito Sans", Font.BOLD, 20), COLOR_SUCCESS, SwingConstants.CENTER); // Feedback imediato
        lblFeedback.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // Espaço acima da mensagem

        card.add(txtExercise, BorderLayout.CENTER); // Texto do exercício no meio do cartão
        card.add(lblFeedback, BorderLayout.SOUTH); // Feedback no rodapé do cartão
        center.add(card, BorderLayout.CENTER); // Cartão no meio da tela

        // Linha de entrada (onde o usuário digita)
        JPanel inputRow = new JPanel(new BorderLayout(10, 0)); // Painel para o campo de texto
        inputRow.setOpaque(false); // Transparente
        inputRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0)); // Espaço acima

        JLabel promptLabel = label("Digite aqui:  ", FONT_BODY, COLOR_TEXT, SwingConstants.LEFT); // Texto de auxílio
        promptLabel.setPreferredSize(new Dimension(165, 52)); // Largura fixa para alinhar com o campo

        txtInput = new JTextField(); // Campo de texto para entrada do usuário
        txtInput.setFont(FONT_INPUT); // Fonte grande para facilitar a leitura
        txtInput.setPreferredSize(new Dimension(0, 52)); // Altura confortável para o campo
        // Estética moderna: Campo de entrada arredondado estilo "search bar"
        txtInput.putClientProperty("JTextField.placeholderText", "Comece a digitar aqui...");
        txtInput.putClientProperty("JComponent.roundRect", true);

        txtInput.addKeyListener(new KeyAdapter() { // Escuta cada tecla pressionada
            @Override public void keyReleased(KeyEvent e) { // Quando o usuário solta a tecla...
                if (active) onInput(); // ...se o jogo estiver ativo, processa a entrada
            }
        });

        inputRow.add(promptLabel, BorderLayout.WEST); // Texto "Digite aqui" na esquerda
        inputRow.add(txtInput,    BorderLayout.CENTER); // Campo de texto no resto da linha
        center.add(inputRow, BorderLayout.SOUTH); // Coloca a linha de entrada embaixo

        return center; // Retorna a área central completa
    }

    // Rodapé moderno com progresso
    private JPanel createFooterProgress() {
        GradientPanel bar = new GradientPanel(COLOR_ACCENT, COLOR_ACCENT, false);
        bar.setPreferredSize(new Dimension(0, 85)); // Aumentado para comportar a margem inferior
        bar.setLayout(new BorderLayout(15, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 40, 25, 40)); // Margem inferior aumentada para 25

        lblProgress = label("Exercicio 1 de 47", FONT_SMALL, Color.WHITE, SwingConstants.LEFT);
        lblProgress.setPreferredSize(new Dimension(200, 24)); // Tamanho fixo

        progressBar = new JProgressBar(0, 100); // Barra visual de progresso
        progressBar.setForeground(Color.WHITE);
        progressBar.setBackground(new Color(255, 255, 255, 60));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 18)); // Altura da barra

        JButton menuBtn = createModernButton("Sair", new Color(255, 255, 255, 40));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setPreferredSize(new Dimension(100, 40));
        menuBtn.addActionListener(e -> goMenu());

        bar.add(lblProgress,  BorderLayout.WEST); // Progresso na esquerda
        bar.add(progressBar,  BorderLayout.CENTER); // Barra no centro
        bar.add(menuBtn,      BorderLayout.EAST); // Botão na direita
        return bar; // Retorna o rodapé
    }

    // ================================================================
    //  API Pública chamada pelo TypingGame
    // ================================================================
    public void startGame(Player p) {
        this.player = p;

        this.exercises =
                ExerciseFactory.createCampaign();

        this.exerciseIndex = 0;
        loadExercise();
    }

    public void nextExercise() {

        exerciseIndex++;

        if (exerciseIndex >= exercises.size()) {

            game.showFinalLevelResult(player);

            return;
        }

        loadExercise();
    }

    // ================================================================
    //  Carregamento do Exercício
    // ================================================================
    private void loadExercise() { // Prepara a interface para um novo exercício
        if (exercises == null || exercises.isEmpty()) return; // Proteção contra lista vazia

        current   = exercises.get(exerciseIndex); // Pega o exercício atual da lista
        timeLeft  = current.getTimeLimit(); // Define o tempo baseado no nível do exercício
        active    = true; // Ativa a lógica de digitação
        startTime = System.currentTimeMillis(); // Marca a hora de início para cálculos de velocidade

        refreshTopBar(); // Atualiza os pontos e vidas no topo
        lblCategory.setText(current.getCategory()    + "  -  " + current.getDescription()); // Atualiza título
        lblInstructions.setText(current.getInstructions()); // Atualiza instruções

        int total = exercises.size(); // Total de exercícios disponíveis
        lblProgress.setText("Exercicio " + (exerciseIndex + 1) + " de " + total); // Atualiza texto de progresso
        progressBar.setValue((int)((double) exerciseIndex / total * 100)); // Atualiza barra visual de progresso

        txtInput.setText(""); // Limpa o que estava digitado antes
        txtInput.setEnabled(true); // Garante que o usuário possa digitar
        setFeedback(" ", COLOR_SUCCESS); // Limpa mensagens de feedback
        renderTarget(""); // Renderiza o texto do exercício sem nenhuma cor de acerto/erro ainda

        if (countdown != null && countdown.isRunning()) countdown.stop(); // Para o cronômetro antigo se houver
        countdown = new Timer(1000, e -> tickTimer()); // Cria um novo cronômetro que "bate" a cada 1 segundo
        countdown.start(); // Inicia o tempo

        SwingUtilities.invokeLater(() -> txtInput.requestFocusInWindow()); // Foca o cursor no campo de texto automaticamente
    }

    // ================================================================
    //  Cronômetro
    // ================================================================
    private void tickTimer() { // Executado a cada 1 segundo
        timeLeft--; // Diminui um segundo
        Color timerColor = timeLeft <= 10 ? COLOR_DANGER // Se faltar 10s, fica vermelho (alerta)
                : timeLeft <= 20 ? COLOR_WARNING // Se faltar 20s, fica laranja
                : Color.WHITE; // Caso contrário, fica branco
        lblTimer.setText(timeLeft + "s"); // Atualiza o texto do tempo na tela
        lblTimer.setForeground(timerColor); // Aplica a cor de alerta
        if (timeLeft <= 0) onTimeUp(); // Se o tempo acabar, chama o fim do exercício
    }

    private void onTimeUp() { // Quando o tempo acaba
        if (!active) return; // Se já terminou, não faz nada
        long elapsed = System.currentTimeMillis() - startTime; // Calcula quanto tempo passou
        finishExercise("Tempo esgotado! Não desanime!", COLOR_DANGER, 0, 0, 0, (int)(elapsed / 1000)); // Finaliza com 0 estrelas
    }

    // ================================================================
    //  Processamento da Digitação
    // ================================================================
    private void onInput() { // Chamado cada vez que uma tecla é solta no campo de entrada
        String typed  = txtInput.getText(); // Pega o que o usuário digitou
        String target = current.getTargetText(); // Pega o que o usuário DEVERIA ter digitado

        // Impede de digitar além do tamanho da frase alvo
        if (typed.length() > target.length()) { // Se digitou demais...
            typed = typed.substring(0, target.length()); // ...corta o excesso
            txtInput.setText(typed); // ...e atualiza o campo
        }

        renderTarget(typed); // Re-desenha o texto do exercício com as cores atualizadas

        if (!typed.isEmpty()) { // Se houver algo digitado
            char last     = typed.charAt(typed.length() - 1); // Pega a última letra digitada
            char expected = target.charAt(typed.length() - 1); // Pega a letra que deveria ser a correta
            if (last == expected) { // Se acertou a letra...
                setFeedback("Muito bem!  Continue assim!", COLOR_SUCCESS); // ...mostra incentivo em verde
            } else { // Se errou a letra...
                setFeedback("Ops! Procure a tecla certa com calma.", COLOR_DANGER); // ...orienta em vermelho
            }
        } else { // Se o campo estiver vazio
            setFeedback(" ", COLOR_SUCCESS); // Limpa o feedback
        }

        renderTarget(typed); // Re-desenha o texto do exercício com as cores atualizadas

        if (typed.length() == target.length()) { // Se terminou de digitar a frase inteira
            checkCompletion(typed, target); // Verifica o resultado final
        }
    }

    private void checkCompletion(String typed, String target) { // Verifica os acertos finais e termina
        if (!active) return; // Proteção contra execuções duplas
        active = false; // Desativa a entrada de dados
        if (countdown != null) countdown.stop(); // Para o cronômetro

        long elapsed    = System.currentTimeMillis() - startTime; // Calcula o tempo total gasto
        int  correct    = countCorrect(typed, target); // Conta quantas letras estão certas
        double acc      = (double) correct / target.length(); // Calcula a precisão (0.0 a 1.0)
        int  stars      = current.calculateStars(acc, elapsed); // Calcula as estrelas (0 a 3)
        int  xp         = current.calculateScore(correct, target.length(), elapsed); // Calcula o XP ganho
        int  timeSeconds = (int)(elapsed / 1000); // Converte tempo gasto para segundos
        int  words      = target.trim().split("\\s+").length; // Conta as palavras da frase
        double minutes  = Math.max(elapsed / 60000.0, 1.0 / 60.0); // Converte tempo para minutos (mínimo 1 seg)
        int  wpm        = (int) Math.round(words / minutes); // Calcula Palavras Por Minuto (WPM/PPM)

        String msg; Color col; // Variáveis para a mensagem final de incentivo
        if (stars == 3) { msg = "Perfeito! Você arrasou!";         col = COLOR_SUCCESS;         }
        else if (stars == 2) { msg = "Muito bem! Ótimo trabalho!"; col = new Color(0, 140, 80); }
        else if (stars == 1) { msg = "Bom início! Vai melhorar!";  col = COLOR_WARNING;         }
        else                 { msg = "Tente novamente, não desista!"; col = COLOR_DANGER;        }

        finishExercise(msg, col, stars, xp, wpm, timeSeconds); // Envia os dados para o encerramento
    }

    private void finishExercise(String msg, Color col, int stars, int xp, int wpm, int timeSeconds) { // Finaliza e mostra tela de resultado
        active = false; // Garante que o jogo está parado
        if (countdown != null) countdown.stop(); // Garante que o cronômetro parou
        txtInput.setEnabled(false); // Bloqueia o campo de digitação
        setFeedback(msg, col); // Mostra a mensagem final no centro

        if (stars == 0) { // Se não ganhou nenhuma estrela (falha)
            player.loseLife(); // O jogador perde uma vida
        } else { // Se ganhou ao menos uma estrela (sucesso)
            player.addXP(xp); // Adiciona os pontos de experiência
            player.completeExercise(); // Incrementa o contador de exercícios feitos e a sequência
        }

        refreshTopBar(); // Atualiza os corações e pontos no topo para refletir a mudança

        int fs = stars, fx = xp, fw = wpm, ft = timeSeconds; // Variáveis finais para o timer
        Timer delay = new Timer(900, e -> // Cria um pequeno atraso de 0.9s antes de mudar de tela
                game.showResult(player, fs, fx, current.getDescription(), fw, ft) // Mostra a tela de resultado
        );
        delay.setRepeats(false); // O timer só deve rodar uma vez
        delay.start(); // Inicia o atraso
    }

    // ================================================================
    //  Renderização Estilizada (Coloração letra por letra)
    // ================================================================
    private void renderTarget(String typed) { // Método crítico que pinta as letras na tela
        StyledDocument doc = txtExercise.getStyledDocument(); // Pega o "documento" do componente de texto
        try {
            doc.remove(0, doc.getLength()); // Limpa todo o texto atual para desenhar do zero
        } catch (BadLocationException ex) { /* ignorado */ }

        String target = current.getTargetText(); // A frase correta

        for (int i = 0; i < target.length(); i++) { // Percorre cada letra da frase alvo
            SimpleAttributeSet as = new SimpleAttributeSet(); // Cria um conjunto de estilos (cor, fonte)
            StyleConstants.setFontFamily(as, "Nunito Sans"); // Define a fonte Nunito Sans para o exercício
            StyleConstants.setFontSize(as, 28); // Define o tamanho grande

            if (i < typed.length()) { // Se esta letra já foi digitada pelo usuário
                if (typed.charAt(i) == target.charAt(i)) { // Se o que ele digitou está certo...
                    StyleConstants.setForeground(as, COL_CORRECT_FG); // ...pinta a letra de verde escuro
                    StyleConstants.setBackground(as, COL_CORRECT_BG); // ...e o fundo de verde claro
                } else { // Se o que ele digitou está errado...
                    StyleConstants.setForeground(as, COL_WRONG_FG); // ...pinta a letra de vermelho
                    StyleConstants.setBackground(as, COL_WRONG_BG); // ...e o fundo de rosa
                }
            } else if (i == typed.length()) { // Se esta é a PRÓXIMA letra que ele deve digitar
                StyleConstants.setForeground(as, COL_NORMAL_FG); // Cor de texto normal
                StyleConstants.setBackground(as, COL_CURSOR_BG); // Fundo azul para servir de cursor visual
            } else { // Letras que ele ainda vai chegar lá
                StyleConstants.setForeground(as, COL_NORMAL_FG); // Cor cinza normal
                StyleConstants.setBackground(as, COLOR_WHITE); // Fundo branco normal
            }

            try {
                doc.insertString(doc.getLength(), String.valueOf(target.charAt(i)), as); // Insere a letra com o estilo definido
            } catch (BadLocationException ex) { /* ignorado */ }
        }

        // Centralizar todo o texto dentro do JTextPane
        SimpleAttributeSet center = new SimpleAttributeSet(); // Cria estilo de parágrafo
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER); // Define alinhamento centralizado
        doc.setParagraphAttributes(0, doc.getLength(), center, false); // Aplica a centralização em todo o texto
    }

    // ================================================================
    //  Ajudantes (Helpers)
    // ================================================================
    private void refreshTopBar() { // Atualiza as informações visuais no topo
        if (player == null) return; // Proteção
        lblPlayerName.setText(player.getName()); // Nome do jogador
        lblLevel.setText("Nível " + player.getGameLevel() + " — " + player.getLevelName()); // Nível atual
        lblScore.setText(player.getTotalScore() + " pts"); // Pontos totais
        lblLives.setText(heartsString(player.getLives())); // Converte número de vidas em corações (♥ ♥ ♥)
        lblLives.setForeground(player.getLives() <= 1 ? COLOR_DANGER : new Color(255, 110, 110)); // Vidas críticas em vermelho escuro
        lblTimer.setText(timeLeft + "s"); // Tempo restante
        lblTimer.setForeground(Color.WHITE); // Cor base do tempo

        // Atualiza a nova barra lateral
        lblSideXP.setText(String.valueOf(player.getTotalScore()));
        lblSideLevel.setText(String.valueOf(exerciseIndex + 1));
        lblSideStreak.setText(player.getStreak() + "x");
    }

    private void setFeedback(String msg, Color color) { // Atalho para mudar a mensagem de feedback
        lblFeedback.setText(msg); // Define o texto
        lblFeedback.setForeground(color); // Define a cor
    }

    private int countCorrect(String typed, String target) { // Conta letras corretas
        int n = 0; // Começa com zero
        for (int i = 0; i < Math.min(typed.length(), target.length()); i++) { // Compara até o limite do digitado
            if (typed.charAt(i) == target.charAt(i)) n++; // Se bater, soma um
        }
        return n; // Retorna o total
    }

    private void goMenu() { // Volta para a tela inicial
        if (countdown != null && countdown.isRunning()) countdown.stop(); // Garante que o tempo pare ao sair
        active = false; // Desativa o jogo
        game.returnToMenu(); // Chama o método de navegação da janela principal
    }
}
