package motor;

public class Score {

    private int scorePlayer;
    private String nomePlayer;

    public Score(int scorePlayer) {
        this.scorePlayer = 0;
        this.nomePlayer = "";
    }

    public void pontuar(int pontos) {
        this.scorePlayer += pontos;
    }

    public String getNomePlayer() {
        return this.nomePlayer;
    }

    public int getScorePlayer() {
        return this.scorePlayer;
    }

    public void setNomePlayer(String nomePlayer) {
        this.nomePlayer = nomePlayer;
    }

}
