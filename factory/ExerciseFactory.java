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
        return all;
    }

    // ----------------------------------------------------------------
    // Nível 1  —  Frases muito curtas, palavras simples
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel1() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("eu amo minha casa.",
                "O lar é o melhor lugar", 1));
        list.add(new SentenceExercise("o sol é muito lindo.",
                "Admirando a natureza", 1));
        list.add(new SentenceExercise("eu gosto de flores.",
                "Amor pelas plantas", 1));
        list.add(new SentenceExercise("Minha família é boa.",
                "Gratidão pela família", 1));
        list.add(new SentenceExercise("O gato dorme no sofá.",
                "O bichinho da casa", 1));
        list.add(new SentenceExercise("Hoje o dia está lindo.",
                "Apreciando o dia", 1));
        list.add(new SentenceExercise("Eu tomo café todo dia.",
                "Hábito matinal", 1));
        list.add(new SentenceExercise("Bom dia para você!",
                "Cumprimento com carinho", 1));
        list.add(new SentenceExercise("Eu gosto de andar no parque.",
                "Passeio ao ar livre", 1));
        list.add(new SentenceExercise("Meu jardim tem muitas flores.",
                "Cuidando do jardim", 1));
        list.add(new SentenceExercise("A vida é muito bonita.",
                "Gratidão pela vida", 1));
        list.add(new SentenceExercise("Eu gosto de ouvir música.",
                "Alegria na música", 1));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 2  —  Frases curtas com pontuação simples
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel2() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise("Como você está se sentindo hoje?",
                "Pergunta de carinho", 2));
        list.add(new SentenceExercise("Minha neta veio me visitar ontem.",
                "Visita da família", 2));
        list.add(new SentenceExercise("Eu gosto de comer bolo de chocolate.",
                "Sabores favoritos", 2));
        list.add(new SentenceExercise("O pássaro cantou na janela de manhã.",
                "Natureza ao redor", 2));
        list.add(new SentenceExercise("Hoje eu fui caminhar no parque.",
                "Cuidando da saúde", 2));
        list.add(new SentenceExercise("Meu neto me ensinou a usar o celular.",
                "Aprendendo com os jovens", 2));
        list.add(new SentenceExercise("Eu preparei um almoço gostoso hoje.",
                "Prazer em cozinhar", 2));
        list.add(new SentenceExercise("A tarde está fresca e muito agradável.",
                "Apreciando o clima", 2));
        list.add(new SentenceExercise("Gosto de regar as plantas todo dia.",
                "Cuidado com as plantas", 2));
        list.add(new SentenceExercise("Minha família se reuniu no domingo.",
                "Domingo em família", 2));
        list.add(new SentenceExercise("Hoje lerei um livro muito interessante.",
                "O prazer da leitura", 2));
        list.add(new SentenceExercise("Eu ligo para minha filha toda semana.",
                "Mantendo o contato", 2));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 3  —  Frases médias com conteúdo do dia a dia
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel3() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise(
                "Aprendo a usar o computador para falar com a família.",
                "Tecnologia para se conectar", 3));
        list.add(new SentenceExercise(
                "Toda manhã eu tomo café e olho o jardim florido.",
                "Rotina matinal com alegria", 3));
        list.add(new SentenceExercise(
                "Meus filhos e netos são a minha maior alegria na vida.",
                "Amor pela família", 3));
        list.add(new SentenceExercise(
                "O médico disse que caminhar faz muito bem para a saúde.",
                "Conselho de saúde", 3));
        list.add(new SentenceExercise(
                "Hoje vou enviar uma mensagem para minha amiga querida.",
                "Amizade e comunicação", 3));
        list.add(new SentenceExercise(
                "Preparei um bolo para o aniversário do meu neto hoje.",
                "Celebrando com amor", 3));
        list.add(new SentenceExercise(
                "Gosto de sentar na varanda e apreciar o pôr do sol.",
                "Momentos de paz", 3));
        list.add(new SentenceExercise(
                "Aprender coisas novas me faz sentir feliz e animado.",
                "Alegria no aprendizado", 3));
        list.add(new SentenceExercise(
                "Minha receita de bolo de laranja é famosa na família.",
                "Tradição culinária", 3));
        list.add(new SentenceExercise(
                "Com paciência e dedicação eu aprendo cada vez mais.",
                "O valor da prática", 3));
        list.add(new SentenceExercise(
                "O jardim da minha casa está cheio de rosas vermelhas.",
                "Beleza no jardim", 3));
        list.add(new SentenceExercise(
                "Ligar para os amigos me deixa mais alegre e animada.",
                "O poder da amizade", 3));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 4  —  Frases mais longas com reflexões do cotidiano
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel4() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise(
                "Cada dia que passa aprendendo algo novo me faz sentir muito bem.",
                "Crescimento pessoal", 4));
        list.add(new SentenceExercise(
                "Hoje liguei para minha filha e conversamos por mais de uma hora.",
                "Conversas que aquecem o coração", 4));
        list.add(new SentenceExercise(
                "O computador me ajuda a ver fotos e falar com quem eu amo.",
                "Tecnologia e afeto", 4));
        list.add(new SentenceExercise(
                "Tenho muito orgulho de estar aprendendo a usar o teclado bem.",
                "Orgulho do progresso", 4));
        list.add(new SentenceExercise(
                "Minha neta disse que eu estou melhorando muito na digitação.",
                "Elogio que emociona", 4));
        list.add(new SentenceExercise(
                "Acordei cedo hoje e fui ao mercado buscar frutas e verduras frescas.",
                "Dia a dia ativo", 4));
        list.add(new SentenceExercise(
                "Saúde, paz e amor ao lado da família são as coisas mais importantes.",
                "Os valores da vida", 4));
        list.add(new SentenceExercise(
                "Com um pouco de prática todo dia eu vou digitar cada vez melhor.",
                "Confiança no progresso", 4));
        list.add(new SentenceExercise(
                "Passei a tarde toda olhando fotos antigas e lembrando momentos felizes.",
                "Memórias guardadas com carinho", 4));
        list.add(new SentenceExercise(
                "Meu filho instalou o programa e agora eu jogo com ele pela internet.",
                "Tecnologia que aproxima", 4));
        return list;
    }

    // ----------------------------------------------------------------
    // Nível 5  —  Frases completas e elaboradas
    // ----------------------------------------------------------------
    private static List<Exercise> createLevel5() {
        List<Exercise> list = new ArrayList<>();
        list.add(new SentenceExercise(
                "A vida fica mais rica e bonita quando a gente continua aprendendo sempre.",
                "Sabedoria de viver", 5));
        list.add(new SentenceExercise(
                "Agradeço todos os dias pelas pessoas maravilhosas que tenho na minha vida.",
                "Gratidão que enche o coração", 5));
        list.add(new SentenceExercise(
                "Com saúde, disposição e muita vontade de aprender tudo se torna possível.",
                "Determinação e alegria", 5));
        list.add(new SentenceExercise(
                "Minha vida mudou muito depois que aprendi a me comunicar pelo computador.",
                "Transformação pela tecnologia", 5));
        list.add(new SentenceExercise(
                "Não importa a idade que temos, pois sempre é hora de aprender algo novo.",
                "Nunca é tarde para aprender", 5));
        list.add(new SentenceExercise(
                "Cada tecla que digito certo me deixa mais feliz e orgulhosa de mim mesma.",
                "Conquistas que emocionam", 5));
        list.add(new SentenceExercise(
                "Os momentos ao lado da família são os que mais ficam guardados no coração.",
                "O tesouro dos momentos", 5));
        list.add(new SentenceExercise(
                "Hoje enviei uma mensagem para todos os meus amigos dizendo que os amo muito.",
                "Amor expresso com palavras", 5));
        return list;
    }
}
