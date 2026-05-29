package factory;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseFactory {

    public static List<Exercise> createAllExercises() {
        List<Exercise> all = new ArrayList<>();
        all.addAll(createLevel1());
        all.addAll(createLevel2());
        all.addAll(createLevel3());
        all.addAll(createLevel4());
        all.addAll(createLevel5());
        all.addAll(createLevel6());
        all.addAll(createLevel7());
        all.addAll(createLevel8());
        return all;
    }

    // ----------------------------------------------------------------
    // Nível 1 — Vogais simples (a e i o u)
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel1() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("a a a a a", "Letra a", 1));
        list.add(new SentenceExercise("e e e e e", "Letra e", 1));
        list.add(new SentenceExercise("i i i i i", "Letra i", 1));
        list.add(new SentenceExercise("o o o o o", "Letra o", 1));
        list.add(new SentenceExercise("u u u u u", "Letra u", 1));
        list.add(new SentenceExercise("a e i a e i", "Vogais a e i", 1));
        list.add(new SentenceExercise("o u a o u a", "Vogais o u a", 1));
        list.add(new SentenceExercise("a e i o u", "Todas as vogais", 1));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 2 — Consoantes comuns (m n r s t l p b c)
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel2() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("ma me mi", "Sílabas com M", 2));
        list.add(new SentenceExercise("na ne ni", "Sílabas com N", 2));
        list.add(new SentenceExercise("ra re ri", "Sílabas com R", 2));
        list.add(new SentenceExercise("sa se si", "Sílabas com S", 2));
        list.add(new SentenceExercise("ta te ti", "Sílabas com T", 2));
        list.add(new SentenceExercise("la le li", "Sílabas com L", 2));
        list.add(new SentenceExercise("pa pe pi", "Sílabas com P", 2));
        list.add(new SentenceExercise("ba be bi bo bu", "Sílabas com B", 2));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 3 — Vogais acentuadas (ã â á à é ê í ó ô ú ç)
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel3() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("á é í ó ú", "Acento Agudo: Segure o botão de acento agudo ao lado da letra P e aperte a vogal", 3));
        list.add(new SentenceExercise("ã õ ã õ", "Til: Segure o botão do til ao lado do c cedilha e aperte a vogal", 3));
        list.add(new SentenceExercise("A E I O U", "Teclas maiúsculas: Aperte o botão Caps Lock no lado esquerdo do teclado ou segure shift ao apertar a tecla", 3));
        list.add(new SentenceExercise("â ê î ô û", "Circunflexo: Segure a tecla Shift (seta para cima no canto inferior esquerdo do teclado), a tecla do til e aperte a vogal", 3));
        list.add(new SentenceExercise("à è ì ò ù", "Crase: Segure a tecla Shift (seta no canto inferior esquerdo do teclado), a tecla do acento agudo e aperte a vogal ", 3));
        list.add(new SentenceExercise("ç ç ç ç", "Cedilha", 3));
        list.add(new SentenceExercise("mãe pão não", "Palavras com til", 3));
        list.add(new SentenceExercise("café água açúcar", "Palavras do cotidiano", 3));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 4 — Pontuação (. , : ; ! ?)
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel4() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("sim. não.", "Ponto final", 4));
        list.add(new SentenceExercise("pão, leite, café", "Vírgula", 4));
        list.add(new SentenceExercise("atenção: leia.", "Dois-pontos", 4));
        list.add(new SentenceExercise("devagar; com calma.", "Ponto e vírgula", 4));
        list.add(new SentenceExercise("que bom!", "Exclamação", 4));
        list.add(new SentenceExercise("tudo bem?", "Interrogação", 4));
        list.add(new SentenceExercise("bom dia! tudo bem?", "Exclamação e interrogação", 4));
        list.add(new SentenceExercise("leite, pão e café.", "Vírgula e ponto final", 4));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 5 — Frases de 2 a 3 palavras sem acento
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel5() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("bom dia", "Cumprimento", 5));
        list.add(new SentenceExercise("boa tarde", "Cumprimento da tarde", 5));
        list.add(new SentenceExercise("com licença", "Educacao no dia a dia", 5));
        list.add(new SentenceExercise("muito obrigado", "Gratidao simples", 5));
        list.add(new SentenceExercise("tudo bem", "Pergunta simples", 5));
        list.add(new SentenceExercise("ate logo", "Despedida", 5));
        list.add(new SentenceExercise("por favor", "Pedido educado", 5));
        list.add(new SentenceExercise("de nada", "Resposta gentil", 5));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 6 — Frases de 2 a 3 palavras com acento e pontuação
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel6() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("Bom dia!", "Cumprimento com pontuação", 6));
        list.add(new SentenceExercise("Tudo bem?", "Pergunta com pontuação", 6));
        list.add(new SentenceExercise("Com licença.", "Educação no dia a dia", 6));
        list.add(new SentenceExercise("Muito obrigado!", "Gratidão com ênfase", 6));
        list.add(new SentenceExercise("Até logo!", "Despedida animada", 6));
        list.add(new SentenceExercise("Por favor.", "Pedido educado", 6));
        list.add(new SentenceExercise("Que ótimo!", "Reação positiva", 6));
        list.add(new SentenceExercise("Boa noite!", "Cumprimento noturno", 6));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 7 — Frases curtas de 4 a 6 palavras
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel7() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("Eu tomo café todo dia.",
                "Hábito matinal", 7));
        list.add(new SentenceExercise("O mercado abre às oito.",
                "Rotina de compras", 7));
        list.add(new SentenceExercise("Hoje eu fui ao banco.",
                "Tarefa do dia", 7));
        list.add(new SentenceExercise("Minha filha me ligou hoje.",
                "Contato com a família", 7));
        list.add(new SentenceExercise("O ônibus passa às sete.",
                "Transporte público", 7));
        list.add(new SentenceExercise("Preciso comprar pão amanhã.",
                "Lembrança do dia a dia", 7));
        list.add(new SentenceExercise("O médico atende às três.",
                "Consulta médica", 7));
        list.add(new SentenceExercise("Eu gosto de chá quente.",
                "Preferência pessoal", 7));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 8 — Frases médias de 7 a 10 palavras
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel8() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise(
                "Amanhã vou ao mercado comprar frutas e verduras.",
                "Planejando as compras", 8));
        list.add(new SentenceExercise(
                "Minha consulta com o médico é na quinta-feira.",
                "Compromisso de saúde", 8));
        list.add(new SentenceExercise(
                "Eu preciso ligar para a farmácia mais tarde.",
                "Tarefa da tarde", 8));
        list.add(new SentenceExercise(
                "O almoço de hoje foi arroz, feijão e frango.",
                "Refeição do dia", 8));
        list.add(new SentenceExercise(
                "Meu neto vai me visitar no final de semana.",
                "Visita da família", 8));
        list.add(new SentenceExercise(
                "Hoje paguei as contas e fui ao correio.",
                "Dia de resolver pendências", 8));
        list.add(new SentenceExercise(
                "O vizinho me ajudou a carregar as compras.",
                "Gentileza no bairro", 8));
        list.add(new SentenceExercise(
                "Preciso levar o cachorro ao veterinário amanhã.",
                "Cuidado com o animal", 8));
        return list;
    }
}