package no.priv.bang.maven.repository.snapshotpruner;

public class Bean {

    private String saksbehandler;
    boolean laast;

    public Bean(String saksbehandler) {
        this.saksbehandler = saksbehandler;
        this.laast = false;
    }

    public String getSaksbehandler() {
        return saksbehandler;
    }

    public boolean isLaast() {
        return laast;
    }

    public void setLaast(boolean laast) {
        this.laast = laast;
    }

}
