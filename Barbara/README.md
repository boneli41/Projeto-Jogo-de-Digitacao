# TeclaFácil — Jogo Educativo de Digitação para Idosos

Aplicação desktop desenvolvida em **Java Swing** com o objetivo de auxiliar idosos no desenvolvimento de habilidades básicas de uso do teclado e interação com o computador, por meio de exercícios de digitação com elementos de gamificação.

---

## Sumário

- [Objetivo](#objetivo)
- [Como Executar](#como-executar)
- [Funcionalidades](#funcionalidades)
- [Mecânica do Jogo](#mecânica-do-jogo)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pilares da POO](#pilares-da-poo)
- [Tecnologias](#tecnologias)

---

## Objetivo

O **TeclaFácil** foi projetado pensando no público idoso, com foco em:

- Interface com **fontes grandes** (20–32pt) e **cores de alto contraste**
- Progressão gradual do nível de dificuldade
- Mensagens **encorajadoras** em Português
- Tempo generoso por exercício (2 a 6 minutos)
- Feedback visual imediato letra por letra

---

## Como Executar

### Pré-requisito

- **Java JDK 8** ou superior instalado
- Verificar com: `java -version`

### Windows

```bat
compilar.bat    ← compila todos os arquivos .java
executar.bat    ← inicia o jogo
```

### Terminal (qualquer SO)

```bash
# Compilar
javac -d out -encoding UTF-8 \
  Main.java \
  model/Player.java model/Exercise.java \
  model/LetterExercise.java model/WordExercise.java model/SentenceExercise.java \
  factory/ExerciseFactory.java \
  ui/TypingGame.java \
  ui/panels/BasePanel.java ui/panels/MenuPanel.java \
  ui/panels/GamePanel.java ui/panels/ResultPanel.java

# Executar
java -cp out Main
```

---

## Funcionalidades

### Gamificação

| Elemento | Descrição |
|---|---|
| **Estrelas (★★★)** | Avaliação de 0 a 3 estrelas por exercício (precisão + velocidade) |
| **XP e Níveis** | Cada exercício concluído rende pontos; acumular XP sobe o nível |
| **Vidas (♥♥♥)** | Três vidas; perde uma ao terminar com 0 estrelas ou esgotar o tempo |
| **Sequência** | Contador de exercícios completados sem perder vida |
| **Conquistas** | Medalhas desbloqueadas por marcos (ex: "Primeiro Passo", "Imparável") |

### Progressão de Níveis

| Nível | Nome | XP necessário |
|---|---|---|
| 1 | Iniciante | 0 |
| 2 | Aprendiz | 100 pts |
| 3 | Intermediário | 300 pts |
| 4 | Avançado | 600 pts |
| 5 | Especialista | 1.000 pts |

### Exercícios (44 frases completas)

| Nível | Exemplo de frase | Tempo |
|---|---|---|
| 1 | `"O sol é muito lindo."` | 2,5 min |
| 2 | `"Como você está se sentindo hoje?"` | 3 min |
| 3 | `"Toda manhã eu tomo café e olho o jardim florido."` | 4 min |
| 4 | `"Cada dia que passa aprendendo algo novo me faz sentir muito bem."` | 5 min |
| 5 | `"Não importa a idade que temos, pois sempre é hora de aprender algo novo."` | 6 min |

### Tela de Resultado

Após cada exercício são exibidos:

- Avaliação em estrelas
- XP ganho e barra de progresso
- **PPM** — Palavras Por Minuto digitadas
- **Tempo** — duração formatada em `m:ss`
- Vidas restantes, pontuação total, sequência e nível atual

---

## Mecânica do Jogo

### Como ganhar pontos

```
XP = recompensa_base × precisão × (1 + bônus_velocidade)
```

- **Precisão** = letras corretas ÷ total de letras (0% a 100%)
- **Bônus de velocidade** = até +50% para quem termina antes da metade do tempo

### Como ganhar estrelas

| Estrelas | Condição |
|---|---|
| ★★★ | Precisão ≥ 95% e tempo usado ≤ 70% do limite |
| ★★ | Precisão ≥ 80% |
| ★ | Precisão ≥ 60% |
| Sem estrela | Precisão < 60% ou tempo esgotado |

### Como perder vida

Você perde **1 vida (♥)** ao terminar com **0 estrelas** (erro excessivo ou tempo esgotado).
Com 0 vidas o jogo encerra e é necessário voltar ao menu para recomeçar.

### Feedback em tempo real

Cada letra digitada recebe coloração instantânea:

- 🟢 **Verde** — letra correta
- 🔴 **Vermelho** — letra errada
- 🔵 **Azul claro** — posição atual do cursor

---

## Estrutura do Projeto

```
TeclaFácil/
│
├── Main.java                        # Ponto de entrada
├── compilar.bat                     # Script de compilação (Windows)
├── executar.bat                     # Script de execução (Windows)
│
├── model/
│   ├── Exercise.java                # Classe abstrata — contrato dos exercícios
│   ├── LetterExercise.java          # Herda Exercise — exercícios de letras
│   ├── WordExercise.java            # Herda Exercise — exercícios de palavras
│   ├── SentenceExercise.java        # Herda Exercise — exercícios de frases
│   └── Player.java                  # Dados e lógica do jogador (XP, vidas, conquistas)
│
├── factory/
│   └── ExerciseFactory.java         # Padrão Factory — cria todos os exercícios
│
└── ui/
    ├── TypingGame.java              # JFrame principal — gerencia navegação (CardLayout)
    └── panels/
        ├── BasePanel.java           # Painel abstrato — estilos e componentes comuns
        ├── MenuPanel.java           # Tela de boas-vindas e entrada do nome
        ├── GamePanel.java           # Tela principal de jogo (timer, input, feedback)
        └── ResultPanel.java         # Tela de resultado (estrelas, PPM, tempo, stats)
```

---

## Pilares da POO

### Abstração
`Exercise` define o contrato de todos os exercícios sem expor detalhes de implementação. Os métodos `getCategory()`, `getInstructions()`, `calculateScore()` e `calculateStars()` são a interface pública usada pelo `GamePanel`.

```java
public abstract class Exercise {
    public abstract String getCategory();
    public abstract String getInstructions();
    public int calculateScore(int correct, int total, long timeMs) { ... }
    public int calculateStars(double accuracy, long timeMs)         { ... }
}
```

### Herança
Três tipos de exercício herdam de `Exercise`, especializando comportamento e parâmetros:

```
Exercise  (abstrato)
├── LetterExercise   — letras isoladas, tempo 90s, XP 20
├── WordExercise     — palavras, tempo variável, XP 30–90
└── SentenceExercise — frases completas, tempo 162–330s, XP 60–300
```

Similarmente, `BasePanel` é a classe-mãe de todos os painéis de tela, centralizando paleta de cores, fontes e criação de botões.

### Encapsulamento
`Player` protege todos os seus dados. A lógica de ganho de XP, progressão de nível e desbloqueio de conquistas vive dentro da classe — o código externo só chama `addXP()` e `completeExercise()`.

```java
public class Player {
    private int lives, xp, gameLevel, streak;   // ninguém acessa diretamente

    public void addXP(int amount) {
        this.xp += amount;
        checkLevelUp();       // lógica interna
        checkAchievements();  // lógica interna
    }
}
```

### Polimorfismo
`GamePanel` trabalha exclusivamente com referências do tipo `Exercise`. O tipo real (`SentenceExercise`, etc.) é resolvido em tempo de execução, permitindo que a tela de jogo sirva qualquer nível sem modificação.

```java
Exercise current = exercises.get(exerciseIndex); // pode ser qualquer subtipo
lblCategory.setText(current.getCategory());       // chama o método correto
lblInstructions.setText(current.getInstructions());
```

---

## Tecnologias

| Item | Detalhe |
|---|---|
| Linguagem | Java 8+ |
| Interface | Java Swing (Metal Look & Feel) |
| Layout | `CardLayout`, `BorderLayout`, `GridLayout`, `BoxLayout`, `GridBagLayout` |
| Texto estilizado | `JTextPane` + `StyledDocument` (coloração letra a letra) |
| Padrão de projeto | **Factory Method** (`ExerciseFactory`) |
| Codificação | UTF-8 (suporte completo a caracteres acentuados) |
