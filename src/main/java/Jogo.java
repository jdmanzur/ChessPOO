package main.java;

import java.io.BufferedWriter;

/**
 * essa classe é responsável pelo gerenciamento do jogo, controlando tudo o que acontece no jogo. 
 *Essa classe contém um tabuleiro, 2 jogadores e o conjunto de 32 peças disponíveis.
 * O jogo sabe o estado em que se encontra a cada momento (por exemplo: início do jogo, xeque, xeque-mate). 
 * Sabe também de que jogador é a vez, controlando as jogadas, as vezes, as checagens, etc, 
 * sendo a principal responsável pela comunicação com os usuários.
 *
 * @author: Jade Manzur de Almeida
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Jogo {
    private Screen screen;
    private Terminal terminal;
    private Scanner scan = new Scanner(System.in);
    private Tabuleiro tabuleiro;
    public Jogador jogador1;
    public Jogador jogador2;
    private EstadosJogoEnum estado;
    private TurnoEnum turno;

    /**
     * Construtor da classe Jogo
     */
    public Jogo() {
        // inicia um novo jogo caso não haja um
        // carrega da memoria caso tenha um arquivo salvo
        carregarJogo();
        // carrega a UI logo em seguida
        try {
            iniciarUI();
        } catch (IOException e) {
            System.out.println("Algo deu errado. Tente novamente :(");
            errorLog("ERRO EM INICIAR UI: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Interage com o usuário, perguntando se deseja carregar um jogo existente se
     * houver un Inicia um jogo novo se não houver jogos salvos ou se for solicitado
     */
    private void carregarJogo() {

        try {
            // tenta abrir o arquivo que salva os objetos
            File save = new File("saves/save");
            File log = new File("saves/log.txt");
            File error = new File("saves/errorlog.txt");

            char opt = ' ';

            // pergunta se o jogador quer continuar o jogo
            if (save.exists()) {
                FileInputStream fi = new FileInputStream(save);
                ObjectInputStream oi = new ObjectInputStream(fi);

                do {
                    System.out.println("Deseja continuar seu jogo?\nS-Sim\nN-Não");
                    opt = scan.nextLine().toUpperCase().charAt(0);

                    // continua o jogo, carregando os objetos do arquivo
                    if (opt == 'S') {

                        // Lê os objetos
                        this.jogador1 = (Jogador) oi.readObject();
                        this.jogador2 = (Jogador) oi.readObject();
                        this.estado = (EstadosJogoEnum) oi.readObject();
                        this.turno = (TurnoEnum) oi.readObject();
                        this.tabuleiro = (Tabuleiro) oi.readObject();

                        oi.close();
                        fi.close();

                    } else if (opt == 'N') {
                        // cria um novo jogo;

                        if (log.exists())
                            log.delete();
                        if (error.exists())
                            error.delete();

                        novoJogo();
                    } else {
                        // entrada inválida
                        System.out.println("Entrada Inválida, por favor digite novamente!");

                    }
                } while (opt != 'S' && opt != 'N');

                save.delete();

            } else {
                novoJogo();
            }

        } catch (IOException io) {
            errorLog("ERRO EM CARREGARJOGO(): " + io.getMessage());
            System.out.println("Erro desconhecido. Iniciando novo jogo.");
            novoJogo();
        } catch (ClassNotFoundException c) {
            errorLog("ERRO EM CARREGARJOGO(): " + c.getMessage());
            System.out.println("Erro desconhecido. Iniciando novo jogo.");
            novoJogo();
        }

    }

    /**
     * Inicia um novo jogo, perguntando o nomes dos jogadores e atribuindo suas
     * peças
     */
    private void novoJogo() {

        CorEnum cor1, cor2;

        System.out.println("Digite o nome do Jogador 1 - (Peças Brancas): ");
        String nome1 = scan.nextLine().toUpperCase();

        System.out.println("Digite o nome do Jogador 2 - (Peças Pretas): ");
        String nome2 = scan.nextLine().toUpperCase();

        cor1 = CorEnum.BRANCO;
        cor2 = CorEnum.PRETO;

        this.jogador1 = new Jogador(nome1, cor1, pegarPecas(cor1));
        this.jogador2 = new Jogador(nome2, cor2, pegarPecas(cor2));

        this.tabuleiro = new Tabuleiro(jogador1.getPecas(), jogador2.getPecas());
        setEstado(EstadosJogoEnum.INICIO);
        setTurno(TurnoEnum.J1);

        try {
            Path path = Paths.get("saves/");
            File save = new File("saves/save");

            if (!save.exists()) {

                Files.createDirectories(path);
                save.createNewFile();
            }

            FileOutputStream f = new FileOutputStream(save);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Escreve os objetos no arquivo
            o.writeObject(this.jogador1);
            o.writeObject(this.jogador2);
            o.writeObject(this.estado);
            o.writeObject(this.turno);
            o.writeObject(this.tabuleiro);

            o.close();
            f.close();

        } catch (IOException io) {
            errorLog("ERRO EM NOVOJOGO(): " + io.getMessage());
            System.out.println("Erro Desconhecido. Fechando Programa.");
            System.exit(1);
        }

    }

    /**
     * Adiciona uma linha de texto em um arquivo, para manter um log de cada
     * movimento feito no jogo
     * 
     * @param s String a ser adicionada no arquivo de log
     */
    private void addLog(String s) {

        try {
            File log = new File("saves/log.txt");
            if (!log.exists())
                log.createNewFile();

            // faz o log de jogadas.
            FileWriter fw = new FileWriter(log, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            out.println(s);

            out.close();
        } catch (IOException io) {
            errorLog("ERRO EM ADDLOG(): " + io.getMessage());
        }
    }

    /**
     * Salva o estado dos objetos e do jogo em um arquivo pré-determinado
     */
    private void salvarJogo() {

        try {
            File save = new File("saves/save");

            if (!save.exists())
                save.createNewFile();

            FileOutputStream f = new FileOutputStream(save);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Escreve os objetos no arquivo
            o.writeObject(this.jogador1);
            o.writeObject(this.jogador2);
            o.writeObject(this.estado);
            o.writeObject(this.turno);
            o.writeObject(this.tabuleiro);

            o.close();
            f.close();

            addLog("JOGADOR 1: " + this.jogador1.getNome() + " (COR: " + this.jogador1.getCor().toString() + ")");
            addLog("JOGADOR 2: " + this.jogador2.getNome() + " (COR: " + this.jogador2.getCor().toString() + ")");
            addLog("ESTADO: " + this.estado.printEstado());
            addLog("TURNO: " + this.turno.printTurno());

            String[] s = this.tabuleiro.mostrarTabuleiro();
            for (int i = 0; i < 11; i++) {
                addLog(s[i]);
            }

        } catch (IOException io) {
            errorLog("ERRO EM SALVARJOGO(): " + io.getMessage());
        }
    }

    /**
     * Cria um vetor de peças baseado em uma cor
     * 
     * @param cor Cor da peças
     * @return retorna um vetor de peças com todas as peças de um jogador de xadrez
     */
    private Peca[] pegarPecas(CorEnum cor) {
        Peca[] p = new Peca[16];

        for (int i = 0; i < 16; i++) {
            if (i < 8) {

                p[i] = new Peao(cor);

            } else if (i < 10) {

                p[i] = new Torre(cor);

            } else if (i < 12) {

                p[i] = new Cavalo(cor);

            } else if (i < 14) {

                p[i] = new Bispo(cor);

            } else if (i == 14) {

                p[i] = new Dama(cor);

            } else if (i == 15) {

                p[i] = new Rei(cor);
            }

        }
        return p;
    }

    /**
     * Retorna o turno atual do jogo, em um enumerate TurnoEnum
     * 
     * @return TurnoEnum
     */
    private TurnoEnum getTurno() {
        return turno;
    }

    /**
     * Retorna o jogador correspondente ao turno atual do jogo
     * 
     * @return Jogador
     */
    private Jogador getJogadorTurno() {
        if (turno == TurnoEnum.J1)
            return jogador1;
        else
            return jogador2;
    }

    /**
     * Seta o turno atual do jogo
     * 
     * @param turno corresponde ao turno a ser setado
     */
    private void setTurno(TurnoEnum turno) {
        this.turno = turno;
    }

    /**
     * Passa o turno do jogo, ou seja, se é a vez do jogador1 passa para o jogador2
     * e vice-versa
     */
    private void passarTurno() {
        if (turno == TurnoEnum.J1) {
            setTurno(TurnoEnum.J2);
        } else {
            setTurno(TurnoEnum.J1);
        }
    }

    /**
     * Retorna o estado atual do jogo em forma de um enumerate EstadosJogoEnum
     * 
     * @return EstadosJogoEnum
     */
    private EstadosJogoEnum getEstado() {
        return estado;
    }

    /**
     * Seta o estado do jogo atual
     * 
     * @param estado corresponde ao estado a ser setado
     */
    private void setEstado(EstadosJogoEnum estado) {
        this.estado = estado;
    }

    /**
     * Escreve na posição i,j da tela da UI o status atual do jogo
     * 
     * @param i coluna da tela
     * @param j linha da tela
     * @throws IOException
     */
    private void status(int i, int j) throws IOException {
        TextGraphics t = this.screen.newTextGraphics();
        t.setForegroundColor(TextColor.ANSI.BLACK);
        t.setBackgroundColor(TextColor.ANSI.BLACK);
        t.putString(i, j, "                       " + getEstado().printEstado());
        t.setForegroundColor(TextColor.ANSI.WHITE);
        t.putString(i, j, "[STATUS]: " + getEstado().printEstado());
        t.putString(i, j + 1, "[TURNO]: " + getTurno().printTurno() + " : " + getJogadorTurno().getNome().toUpperCase()
                + " (COR : " + getJogadorTurno().getCor().toString() + ")");
        screen.refresh();
    }

    /**
     * faz uma jogada, verificando se a peça a ser movimentada pertence ao jogador
     * atual e chamando a função de moverPeca do tabuleiro
     * 
     * dispara uma exceção se qualquer uma das coordenadas estiver fora do alcance
     * do tabuleiro
     * 
     * 
     * @param linhaOrigem   linha de origem da peça a ser movida
     * @param colunaOrigem  coluna de origem da peça a ser movida
     * @param linhaDestino  linha de destino da peça a ser movida
     * @param colunaDestino coluna de destino da peça a ser movida
     * @return retorna verdadeiro se o movimento foi executado com sucesso
     */
    private boolean fazerJogada(int linhaOrigem, char colunaOrigem, int linhaDestino, char colunaDestino) {
        try {
            // tenta acessar a posicao, se for, invalida, joga uma excecao
            // verifica se a cor da peça é a mesma da cor do jogador atual
            if (tabuleiro.getPecaPosicao(linhaOrigem, colunaOrigem).getCor() == getJogadorTurno().getCor()) {
                return tabuleiro.moverPeca(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
            } else {
                log("Essa peça não é sua! Por favor, escolha outra!", TextColor.ANSI.YELLOW);
                return false;
            }

        } catch (NullPointerException nullp) {
            log("Posição inválida!", TextColor.ANSI.YELLOW);
        }
        return false;
    }

    /**
     * Sai do jogo, deletando todos os arquivos de progresso se existirem *
     * 
     * @throws IOException
     */
    private void sair() throws IOException {
        File save = new File("saves/save");
        File log = new File("saves/log");

        if (save.exists())
            save.delete();

        if (log.exists())
            log.delete();

        screen.stopScreen();
        screen.close();
        scan.close();
    }

    /**
     * Método responsável por se comunicar com o usuário e pegar a entrada movendo
     * as posições no tabuleiro de acordo, realizando uma jogada, saindo do jogo,
     * salvando seu estado, etc..
     * 
     * @return boolean - verdadeiro se o jogo foi encerrado, falso se o jogo
     *         continua
     */
    private boolean lerEntrada() {
        // fundo colorido na primeira posição do tabuleiro.
        try {

            moverPosicao(0, 0, 0, 0);

            String s = "";
            int prev_col = 0;
            int prev_lin = 0;
            int atual_col = 0;
            int atual_lin = 0;
            int sel = 0;
            char colunaOrigem = 'A';
            char colunaDestino;
            int linhaOrigem = 0;
            int linhaDestino;

            while (true) {
                prev_col = atual_col;
                prev_lin = atual_lin;
                KeyStroke k = screen.readInput();

                switch (k.getKeyType()) {

                    case End:
                        // salva
                        salvarJogo();
                        screen.stopScreen();
                        screen.close();
                        scan.close();
                        return true;
                    case Escape:
                        // sai sem salvar
                        sair();
                        return true;

                    case Enter:
                        // selecionar posicao
                        sel++;

                        if (sel == 1) {

                            colunaOrigem = ((char) (atual_col + 'A'));
                            linhaOrigem = atual_lin + 1;
                            if (this.tabuleiro.getPosicao(atual_lin, atual_col).isOcupada()) {

                                s = this.tabuleiro.getDesenhoPosicao(atual_lin, atual_col);

                                if (this.tabuleiro.getPecaPosicao(linhaOrigem, colunaOrigem)
                                        .getCor() != getJogadorTurno().getCor()) {
                                    log("Essa peça não é sua. Por favor escolha outra.", TextColor.ANSI.YELLOW);
                                    sel = 0;
                                    colunaOrigem = 'A';
                                    linhaOrigem = 0;
                                    moverPosicao(prev_col, prev_lin, 0, 0);
                                } else {
                                    selecionarPosicao(atual_col, atual_lin);
                                }
                            } else {
                                log("Essa posição está vazia!", TextColor.ANSI.YELLOW);
                                sel = 0;
                                colunaOrigem = 'A';
                                linhaOrigem = 0;
                                moverPosicao(prev_col, prev_lin, 0, 0);
                            }

                        } else if (sel == 2) {
                            colunaDestino = ((char) (atual_col + 'A'));
                            linhaDestino = atual_lin + 1;

                            if (!fazerJogada(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino)) {
                                log("Jogada Inválida! Tente novamente.", TextColor.ANSI.YELLOW);
                                selecionarPosicao((int) colunaOrigem - 65, linhaOrigem - 1);
                                moverPosicao(prev_col, prev_lin, 0, 0);
                                sel = 0;
                            } else {
                                selecionarPosicao((int) colunaOrigem - 65, linhaOrigem - 1);
                                addLog(getJogadorTurno().getNome() + " moveu " + s + " de (" + linhaOrigem + ", "
                                        + colunaOrigem + ") para (" + linhaDestino + ", " + colunaDestino + ")");
                                log("Jogada realizada com sucesso!", TextColor.ANSI.BLUE);
                                return false;
                            }

                        }

                        atual_col = 0;
                        atual_lin = 0;
                        moverPosicao(0, 0, 0, 0);

                        break;

                    case Tab:
                        // deselecionar posicao
                        if (sel == 1) {
                            sel = 0;
                            selecionarPosicao(((int) colunaOrigem) - 65, linhaOrigem - 1);
                            moverPosicao(atual_col, atual_lin, 0, 0);
                            atual_lin = 0;
                            atual_col = 0;
                        }
                        break;
                    case ArrowUp:
                        if (atual_lin > 0)
                            atual_lin--;
                        moverPosicao(prev_col, prev_lin, atual_col, atual_lin);

                        break;
                    case ArrowDown:
                        if (atual_lin < 7)
                            atual_lin++;
                        moverPosicao(prev_col, prev_lin, atual_col, atual_lin);

                        break;
                    case ArrowRight:
                        if (atual_col < 7)
                            atual_col++;
                        moverPosicao(prev_col, prev_lin, atual_col, atual_lin);

                        break;
                    case ArrowLeft:
                        if (atual_col > 0)
                            atual_col--;
                        moverPosicao(prev_col, prev_lin, atual_col, atual_lin);

                        break;
                    default:
                        k = screen.readInput();
                        break;
                }
            }
        } catch (IOException e) {
            errorLog("ERRO EM LERENTRADA(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Método responsável por iniciar o jogo, mostrando a tela de ajuda e depois
     * iniciando um jogo ou saindo forçadamente se um erro ocorreu
     * 
     */
    public void iniciarJogo() {
        try {
            showHelp();
            this.screen.clear();
            jogar();
        } catch (IOException e) {
            errorLog("ERRO EM INICIARJOGO(): " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Método responsável pelo loop do jogo, setando o status adequadamente,
     * renderizando os menus de guia e de status, chamando os métodos de comunicação
     * com o usuário e mostrando o tabuleiro
     */
    private void jogar() {

        boolean quit = false;
        while (!quit) {

            try {

                this.screen.clear();
                help(51, 6);
                renderMenu(25, 6, 5);

                status(51, 1);
                renderMenu(getJogadorTurno().getNome().length() + 40, 3, 0);
                desenharTabuleiro();

                // jogar ou sair
                quit = lerEntrada();

                if (tabuleiro.isEmCheque(CorEnum.PRETO) || tabuleiro.isEmCheque(CorEnum.BRANCO)) {
                    setEstado(EstadosJogoEnum.CHEQUE);

                    boolean branco = tabuleiro.isChequeMate(CorEnum.BRANCO) || jogador1.isReiCapturado();
                    boolean preto = tabuleiro.isChequeMate(CorEnum.PRETO) || jogador2.isReiCapturado();

                    if (preto || branco) {
                        setEstado(EstadosJogoEnum.CHEQUEMATE);

                        status(51, 1);
                        desenharTabuleiro();

                        log("CHEQUE-MATE! Aperte qualquer tecla para encerrar.", TextColor.ANSI.MAGENTA);
                        screen.readInput();

                        // JOGADOR 1 É O BRANCO, PRETO EM CHEQUE MATE
                        if (preto)
                            imprimirVencedor(jogador1.getNome());
                        // JOGADOR 2 É O PRETO, BRANCO EM CHEQUE MATE
                        if (branco)
                            imprimirVencedor(jogador2.getNome());

                        screen.readInput();
                        sair();
                        return;
                    }

                } else {
                    setEstado(EstadosJogoEnum.EM_ANDAMENTO);
                }

                passarTurno();

            } catch (IOException e) {
                errorLog("ERRO EM JOGAR(): " + e.getMessage());
                System.exit(1);
            }
        }

    }

    /**
     * Inicializa todos os parâmetros da UI
     * 
     * @throws IOException
     */
    private void iniciarUI() throws IOException {
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        this.screen.startScreen();

    }

    /**
     * Chama o método mostrarTabuleiro do tabuleiro e mostra na posição adequada o
     * tabuleiro na tela.
     * 
     * @throws IOException
     */
    private void desenharTabuleiro() throws IOException {
        TextGraphics t = this.screen.newTextGraphics();
        t.setForegroundColor(TextColor.ANSI.WHITE);
        t.setBackgroundColor(TextColor.ANSI.BLACK);
        String[] s = this.tabuleiro.mostrarTabuleiro();

        for (int i = 0; i < 11; i++) {
            t.putString(5, 2 + i, s[i]);
        }

        this.screen.refresh();
    }

    /**
     * Recebe valores de linha e coluna de 0 a 7 e colore/descolore posições no
     * tabuleiro, recuperando o desenho original da posição
     * 
     * @param atual_col coluna da posição a ser descolorida
     * @param atual_lin linha da posição a ser descolorida
     * @param i         coluna da posição a ser colorida
     * @param j         linha da posição a ser colorida
     * @throws IOException
     */
    private void moverPosicao(int atual_col, int atual_lin, int i, int j) throws IOException {
        // seleciona uma posicao no tabuleiro

        // se estiver dentro da tabuleiro, descolore o fundo da posicao atual, e colore
        // a nova;
        int tab_i, tab_j;
        tab_i = (atual_col * 3) + 7;
        tab_j = atual_lin + 4;
        TextGraphics t = this.screen.newTextGraphics();
        t.setForegroundColor(TextColor.ANSI.WHITE);

        if (this.tabuleiro.getPosicao(atual_lin, atual_col).isSelecionada()) {
            t.setBackgroundColor(TextColor.ANSI.GREEN);
        } else {
            t.setBackgroundColor(TextColor.ANSI.BLACK);
        }

        t.putString(tab_i, tab_j, this.tabuleiro.getDesenhoPosicao(atual_lin, atual_col));
        screen.refresh();
        tab_i = (i * 3) + 7;
        tab_j = j + 4;

        if (this.tabuleiro.getPosicao(j, i).isSelecionada()) {
            t.setBackgroundColor(TextColor.ANSI.GREEN);
        } else {
            t.setBackgroundColor(TextColor.ANSI.BLACK);
        }

        t.setForegroundColor(TextColor.ANSI.BLUE);
        t.putString(tab_i, tab_j, this.tabuleiro.getDesenhoPosicao(j, i));
        screen.refresh();

    }

    /**
     * Marca uma posição no tabuleiro como selecionada, ou deseleciona uma se já
     * estiver selecionada Colore o fundo da posição selecionada de verde Colore o
     * fundo de uma posição deselecionada de preto
     * 
     * @param atual_col coluna da posição a ser selecionada
     * @param atual_lin linha da posição a ser selecionada
     * @throws IOException
     */
    private void selecionarPosicao(int atual_col, int atual_lin) throws IOException {
        int tab_i, tab_j;
        tab_i = (atual_col * 3) + 7;
        tab_j = atual_lin + 4;
        TextGraphics t = this.screen.newTextGraphics();

        if (!this.tabuleiro.getPosicao(atual_lin, atual_col).isSelecionada()) {
            t.setBackgroundColor(TextColor.ANSI.GREEN);
            this.tabuleiro.getPosicao(atual_lin, atual_col).setSelecionada(true);
        } else {
            t.setBackgroundColor(TextColor.ANSI.BLACK);
            this.tabuleiro.getPosicao(atual_lin, atual_col).setSelecionada(false);
        }

        t.setForegroundColor(TextColor.ANSI.WHITE);
        t.putString(tab_i, tab_j, this.tabuleiro.getDesenhoPosicao(atual_lin, atual_col));
        screen.refresh();

    }

    /**
     * Escreve na tela um log para indicar as mensagens de erro no jogo
     * 
     * @param s   string a ser escrita
     * @param cor cor ANSI para escrever na tela
     */
    private void log(String s, TextColor cor) {
        try {
            TextGraphics t = this.screen.newTextGraphics();
            t.setForegroundColor(TextColor.ANSI.BLACK);
            t.setBackgroundColor(TextColor.ANSI.BLACK);
            t.putString(4, 15, "                                                                              ");
            t.setForegroundColor(cor);
            t.putString(4, 15, "log: " + s);
            this.screen.refresh();

        } catch (IOException e) {
            errorLog("ERRO EM LOG: " + e.getMessage());
        }
    }

    /**
     * Renderiza um menu com caracteres unicode no canto direito da tela
     * 
     * @param i      corresponde à largura do menu
     * @param j      corresponde à altura do menu
     * @param offset corresponde à posição em que o menu será renderizado, em
     *               relação ao eixo vertical
     */
    private void renderMenu(int i, int j, int offset) throws IOException {
        TextGraphics t = this.screen.newTextGraphics();
        t.setForegroundColor(TextColor.ANSI.WHITE);
        t.setBackgroundColor(TextColor.ANSI.BLACK);
        // String[] s = this.tabuleiro.mostrarTabuleiro();
        t.putString(50, offset, "┏");
        t.putString(50 + i, offset, "┓");
        t.putString(50, offset + j, "┗");
        t.putString(50 + i, offset + j, "┛");
        t.drawLine(new TerminalPosition(50 + 1, offset), new TerminalPosition(50 + i - 1, offset), '╍');
        t.drawLine(new TerminalPosition(50, offset + 1), new TerminalPosition(50, j + offset - 1), '┇');
        t.drawLine(new TerminalPosition(50 + i, offset + 1), new TerminalPosition(50 + i, j + offset - 1), '┇');
        t.drawLine(new TerminalPosition(50 + 1, j + offset), new TerminalPosition(50 + i - 1, j + offset), '╍');

        // t.putString(20, 5, s[i]);

        this.screen.refresh();
    }

    /**
     * Mostra na tela um guia com os comandos do jogo
     * 
     * @throws IOException
     */
    private void showHelp() throws IOException {
        this.screen.clear();
        screen.setCursorPosition(null);
        TextGraphics t = this.screen.newTextGraphics();
        t.setForegroundColor(TextColor.ANSI.BLUE);
        t.setBackgroundColor(TextColor.ANSI.BLACK);
        t.putString(15, 5, "Manual - Comandos");

        t.putString(5, 6, "Para movimentar uma peça é necessário escolher sua posiçao inicial e sua posiçao final");

        t.putString(5, 7, "Use as");
        t.putString(13, 7, "SETAS", SGR.BOLD);
        t.putString(19, 7, "para escolher a posiçao no tabuleiro.");

        t.putString(5, 8, "Aperte");
        t.putString(12, 8, "ENTER", SGR.BOLD);
        t.putString(18, 8, "para selecionar a posiçao");

        t.putString(5, 9, "Aperte");
        t.putString(12, 9, "TAB", SGR.BOLD);
        t.putString(16, 9, "para deselecionar a posiçao");

        t.putString(5, 10, "Aperte");
        t.putString(12, 10, "ESC", SGR.BOLD);
        t.putString(16, 10, "para sair do jogo (nao salva o progresso!)");

        t.putString(5, 11, "Aperte");
        t.putString(12, 11, "END", SGR.BOLD);
        t.putString(16, 11, "para salvar e sair do jogo. (assim você pode continuar depois)");

        t.putString(5, 12, "Aperte qualquer tecla para jogar!", SGR.BOLD);
        this.screen.refresh();
        this.screen.readInput();
        return;
    }

    /**
     * Mostra na tela um guia simplificado com os comandos do jogo
     * 
     * @param i coluna em que o texto será mostrado
     * @param j linha em que o texto será mostrado
     */
    private void help(int i, int j) throws IOException {

        TextGraphics t = this.screen.newTextGraphics();
        t.setForegroundColor(TextColor.ANSI.WHITE);
        t.setBackgroundColor(TextColor.ANSI.BLACK);
        t.putString(i, j, "> SETAS: mover");
        t.putString(i, j + 1, "> ENTER: selecionar");
        t.putString(i, j + 2, "> TAB: deselecionar");
        t.putString(i, j + 3, "> END: salvar e sair");
        t.putString(i, j + 4, "> ESC: sair");

        screen.refresh();
    }

    /**
     * Imprime uma tela que mostra vencedor do jogo
     * 
     * @param vencedor nome do vencedor
     * @throws IOException
     */
    private void imprimirVencedor(String vencedor) throws IOException {
        screen.clear();
        TextGraphics t = this.screen.newTextGraphics();
        t.setForegroundColor(TextColor.ANSI.YELLOW);
        t.setBackgroundColor(TextColor.ANSI.BLACK);
        t.putString(35, 5, "[GAME OVER]", SGR.BOLD);
        t.putString(40, 7, "@", SGR.BLINK);
        t.putString(38, 8, "@:::@", SGR.BLINK);
        t.putString(35, 9, "@.:/\\:/\\:.@", SGR.BLINK);
        t.putString(34, 10, "':\\@ @ @ @/:'", SGR.BLINK);
        t.putString(36, 11, "[@W@W@W@]", SGR.BLINK);
        t.putString(35, 12, "{><><@><><}", SGR.BLINK);
        t.putString(35, 13, "^^^^^^^^^^^", SGR.BLINK);
        t.putString(25, 15, vencedor.toUpperCase() + " venceu o jogo! Parabéns!");
        screen.refresh();
    }

    /**
     * Método que adiciona uma linha ao arquivo de erro do programa
     * 
     * @param s String a ser escrita no arquivo de erro
     */
    private void errorLog(String s) {
        try {
            File log = new File("saves/errorlog.txt");
            if (!log.exists())

                log.createNewFile();

            // faz o log de erros
            FileWriter fw = new FileWriter(log, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            out.println(s);

            out.close();
        } catch (IOException io) {
            // sai forçadamente do programa
            System.exit(1);
        }
    }
}
