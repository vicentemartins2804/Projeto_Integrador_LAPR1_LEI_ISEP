import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.io.BufferedWriter;

//Indíce Código
// - Parte Interativa → linha 66
// - Parte Não Interativa → linha 329
// - Parte Global → linha 422
// - Testes / Verificações → linha 626

public class AppLAPR1 {
    public static final String[] ESTADO_INICIAL = {"S0","I0","R0"};

    private static final String[] PARAMETROS_INICIAIS = {"caso","lambda","mu","kapa","beta","b","delta1","delta2"};

    public static final int NUM_ARGS_NINTERAC = 10;

    public static final int NUM_PARAMETROS_FICHEIRO = 8;

    public static final int NUM_EST_INICIAL_FICHEIRO = 3;

    private static boolean testeUnitarioRealizadoEstIni = false;

    private static boolean testeUnitarioRealizadoParamIni = false;

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length > 0) {

            //Teste unitario
            boolean lengthCorreto = verificarNumeroArgumentosInicialNInterativo(args,NUM_ARGS_NINTERAC);
            System.out.println("Numeros argumentos igual a 10? " + lengthCorreto);

            String nomeDoArquivoParametros = null, nomeDoArquivoInicial = null, m = null, p = null, d = null;

            for (int i = 0; i < args.length - 1; i += 2){
                switch (args[i]) {
                    case "-b":
                        nomeDoArquivoParametros = args[i + 1];
                    case "-c":
                        nomeDoArquivoInicial = args[i + 1];
                    case "-m":
                        m = args[i + 1];
                    case "-p":
                        p = args[i + 1];
                    case "-d":
                        d = args[i + 1];

                }
            }
            verificacaoNrArgumentos(args);
            verficacoesArgumentos(nomeDoArquivoParametros, nomeDoArquivoInicial, m, p, d);

        } else {
            exibirMenu();
            menuInterativo();
        }

    }

    //################################################################################################################
    //############################################## PARTE INTERATIVA ################################################
    //################################################################################################################

    // Menu que aparece para a aplicação interativa
    public static void exibirMenu() {
        System.out.println("======================================================");
        System.out.println("       Modelaçao do consumo epidémico de drogas       ");
        System.out.println("======================================================");
        System.out.println("[1] Método de Euler");
        System.out.println("[2] Método de Runge-Kutta de 4ª ordem");
        System.out.println("[9] Mostrar Menu");
        System.out.println("[0] Sair");
    }

    // Menu que pede para o utilizador fazer uma escolha
    public static void menuInterativo() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        int caso;
        do {
            System.out.println();
            System.out.print("Escolha uma opçao: ");
            caso = scanner.nextInt();
            switch (caso) {
                case 1:
                    System.out.println("Você escolheu a opçao 1 - Método de Euler.");
                    modoDadosInterativo(caso);
                    break;
                case 2:
                    System.out.println("Você escolheu a opçao 2 - Método de Runge-Kutta de 4ª ordem.");
                    modoDadosInterativo(caso);
                    break;
                case 9:
                    exibirMenu();
                    break;
                case 0:
                    System.out.println("Saindo do programa...");
                    break;
                default:
                    System.out.println("Opçao inválida. Escolha novamente.");
                    break;
            }
        } while (caso != 0);
        System.exit(0); // Status 0 indica um encerrado normal
    }

    public static void modoDadosInterativo(int caso) throws IOException, InterruptedException {

        System.out.println();
        System.out.println("======================================================");
        System.out.println("                    INSERIR DADOS                     ");
        System.out.println("======================================================");
        System.out.println("[1] Inserir dados por ficheiro");
        System.out.println("[2] Inserir dados manualmente");
        System.out.println("[0] Voltar");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        int dadosinseridos;

        do {
            System.out.println();
            System.out.print("Escolha uma opçao: ");
            dadosinseridos = scanner.nextInt();
            switch (dadosinseridos) {
                case 1:
                    System.out.println("Você escolheu a opçao 1 - Dados inseridos por ficheiro.");
                    leituraNomes(caso);
                    exibirMenu();
                    menuInterativo();
                    break;
                case 2:
                    System.out.println("Você escolheu a opçao 2 - Dados inseridos manualmente.");
                    if (caso == 1)
                        dadosEulerInterativo();
                    else
                        dadosRungeKuttaInterativo();

                    exibirMenu();
                    menuInterativo();
                    break;
                case 9:
                    exibirMenu();
                    break;
                case 0:
                    System.out.println("A voltar ao menu inicial...");
                    System.out.println();
                    exibirMenu();
                    menuInterativo();
                    break;
                default:
                    System.out.println("Opçao inválida. Escolha novamente.");
                    break;
            }
        } while (dadosinseridos != 0);
    }

    public static double[] matrizDadosInterativos() {
        Scanner sc = new Scanner(System.in);
        double[] matrizParametros = new double[12];
        for (int colunas = 0; colunas < 12; colunas++) {
            System.out.printf("Digite o [" + nomeDados(colunas) + "]: ");
            matrizParametros[colunas] = Double.parseDouble(sc.nextLine().replace(",", "."));
            if (colunas == 10 ){
                if (matrizParametros[colunas]  <= 0 ){
                    while (matrizParametros[colunas]  <= 0 ) {
                        System.out.printf("Digite o [" + nomeDados(colunas) + "]: ");
                        matrizParametros[colunas] = Double.parseDouble(sc.nextLine().replace(",", "."));
                    }
                }
            }
            if (colunas == 11 ){
                if ((matrizParametros[colunas]  <= 0) || (matrizParametros[colunas] > 1)){
                    while ((matrizParametros[colunas]  <= 0) || (matrizParametros[colunas] > 1)){
                        System.out.printf("Digite o [" + nomeDados(colunas) + "]: ");
                        matrizParametros[colunas] = Double.parseDouble(sc.nextLine().replace(",", "."));
                    }
                }
            }
        }
        return matrizParametros;
    }

    public static String nomeDados(int colunas) {
        String nome = "";
        switch (colunas) {
            case 0:
                nome = "Lambda";
                break;
            case 1:
                nome = "Mu";
                break;
            case 2:
                nome = "Kapa";
                break;
            case 3:
                nome = "Beta";
                break;
            case 4:
                nome = "b";
                break;
            case 5:
                nome = "Delta1";
                break;
            case 6:
                nome = "Delta2";
                break;
            case 7:
                nome = "S[0]";
                break;
            case 8:
                nome = "I[0]";
                break;
            case 9:
                nome = "R[0]";
                break;
            case 10:
                nome = "Dia";
                break;
            case 11:
                nome = "Tamanho do passo";
                break;
        }
        return nome;
    }

    public static void dadosEulerInterativo() throws IOException, InterruptedException {

        double[] matrizDados = matrizDadosInterativos();

        double lambda = matrizDados[0];
        double mu = matrizDados[1];
        double k = matrizDados[2];
        double beta = matrizDados[3];
        double b = matrizDados[4];
        double delta1 = matrizDados[5];
        double delta2 = matrizDados[6];
        int n = (int) matrizDados[10];
        double h = matrizDados[11];
        int div = (int) Math.floor(n / h);
        double[] S = new double[div + 1];
        double[] I = new double[div + 1];
        double[] R = new double[div + 1];
        S[0] = matrizDados[7];
        I[0] = matrizDados[8];
        R[0] = matrizDados[9];
        System.out.println();
        metodoEuler(lambda, b, k, beta, mu, delta1, delta2, S, I, R, n, h);

    }

    public static void dadosRungeKuttaInterativo() throws IOException, InterruptedException {

        double[] matrizDados = matrizDadosInterativos();

        double lambda = matrizDados[0];
        double mu = matrizDados[1];
        double k = matrizDados[2];
        double beta = matrizDados[3];
        double b = matrizDados[4];
        double delta1 = matrizDados[5];
        double delta2 = matrizDados[6];
        int n = (int) matrizDados[10];
        double h = matrizDados[11];
        int div = (int) Math.floor(n / h);
        double[] S = new double[div + 1];
        double[] I = new double[div + 1];
        double[] R = new double[div + 1];
        S[0] = matrizDados[7];
        I[0] = matrizDados[8];
        R[0] = matrizDados[9];
        System.out.println();
        metodoRungeKutta(lambda, b, k, beta, mu, delta1, delta2, S, I, R, n, h);

    }

    public static void leituraNomes(int caso) throws IOException, InterruptedException {
        System.out.println();
        Scanner ler = new Scanner(System.in);
        int aux;
        String nomeDoArquivoInicial, nomeDoArquivoParametros;

        do {
            System.out.print("Digite o nome do ficheiro inicial: ");
            nomeDoArquivoInicial = ler.nextLine();
            System.out.println();
            System.out.print("Digite o nome do ficheiro Parâmetros: ");
            nomeDoArquivoParametros = ler.nextLine();
            aux = verificacaoFicheiro(nomeDoArquivoInicial, nomeDoArquivoParametros);

        } while (aux != 2);

        double h = lerPasso();
        int n = lerNrDias();
        dadosMetodos(nomeDoArquivoParametros, nomeDoArquivoInicial, caso, h, n);
    }

    public static int lerNrDias(){
        Scanner ler = new Scanner(System.in);
        int dias;

        do {
            System.out.print("Digite o número de Dias: ");
            dias = ler.nextInt();
            System.out.println();
        }while(dias <= 0);

        return dias;
    }

    public static double lerPasso(){
        Scanner ler = new Scanner(System.in);
        double passo;

        do {
            System.out.print("Digite o valor do Passo: ");
            passo = Double.parseDouble(ler.nextLine().replace(",", "."));
            System.out.println();
        }while ((passo < 0) || (passo > 1));

        return passo;
    }

    //###############################################################################################################
    //###################################### PARTE NÃO INTERATIVA ###################################################
    //###############################################################################################################


    public static void dadosMetodos(String nomeDoArquivoParametros, String nomeDoArquivoInicial, int caso, double h,
                                    int n) throws IOException, InterruptedException {

        double lambda = dadosParametrosIniciais(nomeDoArquivoParametros)[1];
        double mu = dadosParametrosIniciais(nomeDoArquivoParametros)[2];
        double k = dadosParametrosIniciais(nomeDoArquivoParametros)[3];
        double beta = dadosParametrosIniciais(nomeDoArquivoParametros)[4];
        double b = dadosParametrosIniciais(nomeDoArquivoParametros)[5];
        double delta1 = dadosParametrosIniciais(nomeDoArquivoParametros)[6];
        double delta2 = dadosParametrosIniciais(nomeDoArquivoParametros)[7];
        int div = (int) Math.floor(n / h);
        double[] S = new double[div + 1];
        double[] I = new double[div + 1];
        double[] R = new double[div + 1];
        S[0] = dadosEstadoInicial(nomeDoArquivoInicial)[0];
        I[0] = dadosEstadoInicial(nomeDoArquivoInicial)[1];
        R[0] = dadosEstadoInicial(nomeDoArquivoInicial)[2];
        verificacaoEstadoInicial(S, I, R);

        if (caso == 1)
            metodoEuler(lambda, b, k, beta, mu, delta1, delta2, S, I, R, n, h);
        else if (caso == 2)
            metodoRungeKutta(lambda, b, k, beta, mu, delta1, delta2, S, I, R, n, h);
    }

    // Interpreta e converte para double o ficheiro Estado Inicial
    public static double[] dadosEstadoInicial(String nomeDoArquivoInicial) throws FileNotFoundException {
        File EstadoInicial = new File(nomeDoArquivoInicial);
        Scanner sc = new Scanner(EstadoInicial);
        double[] valoresEstadoInicial = new double[3];
        String cabecalho = sc.nextLine();
        String[] cabecalhoArray = cabecalho.split(";");
        String linha = sc.nextLine();
        String linhaComPontos = linha.replace(",", ".");
        String[] items = linhaComPontos.split(";");

        for (int i = 0; i < items.length; i++) {
            valoresEstadoInicial[i] = Double.parseDouble(items[ordenarDados(cabecalhoArray[i], ESTADO_INICIAL)]);
        }

        if (!testeUnitarioRealizadoEstIni) {
            //Teste unitario
            boolean lengthCorreto = verificarNumeroValoresEstadoInicial(valoresEstadoInicial, NUM_EST_INICIAL_FICHEIRO);
            System.out.println("Numero de valores do estado inicial igual a 3? " + lengthCorreto);

            testeUnitarioRealizadoEstIni = true;
        }

        return valoresEstadoInicial;
    }

    // Interpreta e converte para double o ficheiro Parâmetros Iniciais
    public static double[] dadosParametrosIniciais(String nomeDoArquivoParametros) throws FileNotFoundException {
        File ParametrosIniciais = new File(nomeDoArquivoParametros);  //Ficheiro dos Parâmetros Iniciais
        Scanner sc = new Scanner(ParametrosIniciais);
        double[] valoresParametrosIniciais = new double[8];
        String cabecalho = sc.nextLine();
        String[] cabecalhoArray = cabecalho.split(";");
        String linha = sc.nextLine();
        String linhaComPontos = linha.replace(",", ".");
        String[] items = linhaComPontos.split(";");

        for (int i = 0; i < items.length; i++) {
            valoresParametrosIniciais[i] = Double.parseDouble(items[ordenarDados(cabecalhoArray[i], PARAMETROS_INICIAIS)]);
        }

        if (!testeUnitarioRealizadoParamIni) {
            //Teste unitario
            boolean lengthCorreto = verificarNumeroValoresParametrosIniciais(valoresParametrosIniciais, NUM_PARAMETROS_FICHEIRO);
            System.out.println("Numero de valores dos parametros igual a 8? " + lengthCorreto);

            testeUnitarioRealizadoParamIni = true;
        }

        return valoresParametrosIniciais;
    }

    public static int ordenarDados(String nomeVariavel, String[] ordemCerta) {
        int posicao;
        for (posicao = 0; posicao < ordemCerta.length; posicao++) {
            if (ordemCerta[posicao].equals(nomeVariavel)) {
                break;
            }
        }
        return posicao;
    }


    //###############################################################################################################
    //########################## MÉTODOS GLOBAIS / PARTE INTERATIVA E NÃO INTERATIVA ################################
    //###############################################################################################################


    public static void metodoEuler(double lambda, double b, double k, double beta, double mu, double delta1,
                                   double delta2, double[] S, double[] I, double[] R, int n, double h)
            throws IOException, InterruptedException {

        double precisao = 1e-1;
        double aux = 0;
        int v=0;
        double aux2 = 1;
        double div = (n / h);
        double[] outS = new double[n + 1];
        double[] outI = new double[n + 1];
        double[] outR = new double[n + 1];
        outS[0] = S[0];
        outI[0] = I[0];
        outR[0] = R[0];
        for (int i = 1; i <= div; i++) {
            double dSdt = lambda - b * S[i - 1] * I[i - 1] - mu * S[i - 1];
            double dIdt = b * S[i - 1] * I[i - 1] - k * I[i - 1] + beta * I[i - 1] * R[i - 1] - (mu + delta1) * I[i - 1];
            double dRdt = k * I[i - 1] - beta * I[i - 1] * R[i - 1] - (mu + delta2) * R[i - 1];

            aux = aux + h;
            S[i] = S[i - 1] + h * dSdt;
            I[i] = I[i - 1] + h * dIdt;
            R[i] = R[i - 1] + h * dRdt;

            if (Math.abs(aux2 - aux) < precisao) {
                aux2=aux2+1;
                v=v+1;
                outS[v] = S[i];
                outI[v] = I[i];
                outR[v] = R[i];

            }
        }


        impressaoGrafico(outS, outI, outR, n);
        juntarMatrizes(outS, outI, outR, n);
    }

    public static void metodoRungeKutta(double lambda, double b, double k, double beta, double mu,
                                        double delta1, double delta2, double[] S, double[] I, double[] R,
                                        int n, double h) throws IOException, InterruptedException {
        double precisao = 1e-1;
        double aux = 0;
        int v = 0;
        double div = (n / h);
        double aux2=1;
        double[] outS = new double[n + 1];
        double[] outI = new double[n + 1];
        double[] outR = new double[n + 1];
        outS[0] = S[0];
        outI[0] = I[0];
        outR[0] = R[0];

        for (int i = 1; i <= div; i++) {
            double[] k1 = calcularDerivadas(lambda, b, k, beta, mu, delta1, delta2, S[i - 1], I[i - 1], R[i - 1]);
            double[] k2 = calcularDerivadas(lambda, b, k, beta, mu, delta1, delta2,
                    S[i - 1] + h / 2 * k1[0], I[i - 1] + h / 2 * k1[1], R[i - 1] + h / 2 * k1[2]);
            double[] k3 = calcularDerivadas(lambda, b, k, beta, mu, delta1, delta2,
                    S[i - 1] + h / 2 * k2[0], I[i - 1] + h / 2 * k2[1], R[i - 1] + h / 2 * k2[2]);
            double[] k4 = calcularDerivadas(lambda, b, k, beta, mu, delta1, delta2,
                    S[i - 1] + h * k3[0], I[i - 1] + h * k3[1], R[i - 1] + h * k3[2]);

            aux = aux + h;
            S[i] = S[i - 1] + h / 6 * (k1[0] + 2 * k2[0] + 2 * k3[0] + k4[0]);
            I[i] = I[i - 1] + h / 6 * (k1[1] + 2 * k2[1] + 2 * k3[1] + k4[1]);
            R[i] = R[i - 1] + h / 6 * (k1[2] + 2 * k2[2] + 2 * k3[2] + k4[2]);

            if (Math.abs(aux2 - aux) < precisao) {
                aux2=aux2+1;
                v=v+1;
                outS[v] = S[i];
                outI[v] = I[i];
                outR[v] = R[i];

            }
        }
        impressaoGrafico(outS, outI, outR, n);
        juntarMatrizes(outS, outI, outR, n);
    }

    //Junta as matrizes de modo a facilitar a impressão
    public static void juntarMatrizes(double[] outS, double[] outI, double[] outR, int n) {

        double[][] matrizTotal = new double[n + 1][5];

        for (int j = 0; j <= 3; j++) {
            for (int i = 0; i <= n; i++) {
                matrizTotal[i][0] = i;
                if (j == 1)
                    matrizTotal[i][1] = outS[i];
                if (j == 2)
                    matrizTotal[i][2] = outI[i];
                else
                    matrizTotal[i][3] = outR[i];
            }
        }
        somaMatriz(matrizTotal, n);
    }

    public static void somaMatriz(double[][] matrizTotal, int n) {

        for (int i = 0; i <= n; i++) {
            for (int j = 1; j < 4; j++) {
                matrizTotal[i][4] = matrizTotal[i][j] + matrizTotal[i][4];
            }
        }
        imprimirCSV(matrizTotal);
    }


    // Método auxiliar para calcular as taxas de variação
    private static double[] calcularDerivadas(double lambda, double b, double k, double beta, double mu,
                                              double delta1, double delta2, double S, double I, double R) {
        double dSdt = lambda - b * S * I - mu * S;
        double dIdt = b * S * I - k * I + beta * I * R - (mu + delta1) * I;
        double dRdt = k * I - beta * I * R - (mu + delta2) * R;

        return new double[]{dSdt, dIdt, dRdt};
    }

    public static void imprimirCSV(double[][] matrizTotal) {
        try {
            // Utiliza o diretório do projeto como base para o caminho do arquivo
            String filePath = "outputCSV.csv";
            File csvFile = new File(filePath);

            // Cria o arquivo se não existir
            if (csvFile.createNewFile()) {
                System.out.println("Arquivo CSV criado com sucesso.");
                System.out.println();
            } else {
                System.out.println("Arquivo CSV já existe. Será sobrescrito.");
                System.out.println();
            }

            // Cria o objeto FileWriter com o caminho do arquivo
            FileWriter fileWriter = new FileWriter(csvFile);

            // Cabeçalho
            String[] cabecalho = {"dia", "S", "I", "R", "T"};
            StringBuilder header = new StringBuilder(String.join(";", cabecalho) + "\n");
            fileWriter.write(header.toString());

            // Configura o formato para 6 casas decimais
            DecimalFormat df = new DecimalFormat("#.######");

            // Adiciona o restante das linhas ao arquivo CSV
            for (double[] data : matrizTotal) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < data.length; i++) {
                    line.append(df.format(data[i]));
                    if (i != data.length - 1) {
                        line.append(';');
                    }
                }
                line.append("\n");
                fileWriter.write(line.toString());
            }

            // Fecha o FileWriter
            fileWriter.close();

            // Abre o arquivo CSV com o aplicativo padrão associado ao tipo de arquivo
            Desktop.getDesktop().open(csvFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void impressaoGrafico(double[] S, double[] I, double[] R, int n)
            throws IOException, InterruptedException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt"));
        for (int dia = 0; dia <= n; dia++) {
            writer.write(dia + " " + S[dia] + " " + I[dia] + " " + R[dia]);
            writer.newLine();
        }
        writer.close();

        String gnuplotCommand = "\"C:\\Program Files\\gnuplot\\bin\\wgnuplot.exe\" -e \"set terminal png; "
                + "set output 'grafico.png'; "
                + "set title 'Modelo Epidemiológico'; "
                + "set xlabel 'Dias'; set ylabel 'Pessoas'; "
                + "plot 'dados.txt' using 1:2 with lines title 'Suscetíveis', "
                + "'dados.txt' using 1:3 with lines title 'Infetados', "
                + "'dados.txt' using 1:4 with lines title 'Recuperados'\"";

        Process process = Runtime.getRuntime().exec(gnuplotCommand);
        process.waitFor();

        // Abre o arquivo PNG com o aplicativo padrão associado ao tipo de arquivo
        File graficoFile = new File("grafico.png");
        Desktop.getDesktop().open(graficoFile);

    }

    //################################################################################################################
    //############################################ TESTES / VERIFICAÇÕES #############################################
    //################################################################################################################

    // ------------------------------------------- NÃO PARTE INTERATIVA ----------------------------------------------

    public static void verificacaoNrArgumentos (String[] args){

        final int argsEsperados = 10;

        if ((argsEsperados != args.length) && (args.length > 0)){
            System.out.println("O número de argumentos está incorreto.");
            System.exit(1); // Status 1 indica um encerrado anormal e forçado
        }
    }

    public static void verficacoesArgumentos(String nomeDoArquivoParametros, String nomeDoArquivoInicial,
                                             String m, String p, String d) throws IOException, InterruptedException {

        int aux = 0;
        int caso = Integer.parseInt(m);
        double h = Double.parseDouble(p);
        int n = Integer.parseInt(d);


        // Obtém o diretório de trabalho atual (pasta local do programa)
        String diretorioAtual = System.getProperty("user.dir");

        // Cria um objeto File para o arquivo na pasta local
        File arquivo1 = new File(diretorioAtual, nomeDoArquivoInicial);
        File arquivo2 = new File(diretorioAtual, nomeDoArquivoParametros);

        if (arquivo1.exists()) {
            aux = aux + 1;
        } else {
            System.out.println("O arquivo inicial nao está na pasta local do programa.");
        }
        if (arquivo2.exists()) {
            aux = aux + 1;
        } else {
            System.out.println("O arquivo Parâmetros nao está na pasta local do programa.");
        }

        if (caso == 1 || caso == 2)
            aux = aux + 1;
        else
            System.out.println("O Caso nao deve ser diferente de 1 e 2.");

        if ((h > 0) && (h < 1))
            aux = aux + 1;
        else
            System.out.println("O Passo deve estar entre <0> e <1>.");


        if (n>0)
            aux = aux + 1;
        else
            System.out.println("O número de Dias deve ser maior que <0>.");

        if (aux == 5)
            dadosMetodos(nomeDoArquivoParametros, nomeDoArquivoInicial, caso, h, n);
        else
            System.exit(1); // Status 1 indica um encerrado anormal e forçado

    }

    // Verificação da soma dos dados do Estado Inicial
    public static void verificacaoEstadoInicial(double[] S, double[] R,double[] I){

        if (S[0] + R[0] + I[0] != 1) {
            System.out.println("A operacao <S[0] + R[0] + I[0]> tem de ser 1");
            System.exit(1);
        }
    }


    // --------------------------------------------- PARTE INTERATIVA ------------------------------------------------


    public static int verificacaoFicheiro (String nomeDoArquivoInicial, String nomeDoArquivoParametros) {
        int aux = 0;

        // Obtém o diretório de trabalho atual (pasta local do programa)
        String diretorioAtual = System.getProperty("user.dir");

        // Cria um objeto File para o arquivo na pasta local
        File arquivo1 = new File(diretorioAtual, nomeDoArquivoInicial);
        File arquivo2 = new File(diretorioAtual, nomeDoArquivoParametros);

        if (arquivo1.exists()) {
            aux = aux + 1;
        } else {
            System.out.println("O arquivo inicial nao está na pasta local do programa.");
        }
        if (arquivo2.exists()) {
            aux = aux + 1;
        } else {
            System.out.println("O arquivo Parâmetros nao está na pasta local do programa.");
        }
        System.out.println();
        return aux;
    }

    // ------------------------------------------ Testes Unitários ---------------------------------------------------

    public static boolean verificarNumeroArgumentosInicialNInterativo (String[] argsArray, int nargsEsperados){
        return argsArray.length == nargsEsperados;
    }

    public static boolean verificarNumeroValoresEstadoInicial (double[] valoresEstadoInicial, int numValEstIni){
        return valoresEstadoInicial.length == numValEstIni;
    }
    public static boolean verificarNumeroValoresParametrosIniciais (double[] valoresParametrosIniciais,
                                                                    int numValParamIni){
        return valoresParametrosIniciais.length == numValParamIni;
    }
}