package music;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;


public class Mp3Player {

    private String filename;
    private Player player;

    public boolean isPlaying = false;


    public Mp3Player(String filename) {
        this.filename = filename;
    }


    public void play() {
        isPlaying = true;
        try {
            BufferedInputStream buffer = new BufferedInputStream(
                    new FileInputStream(filename));
            player = new Player(buffer);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        new Thread(() -> {
            try { player.play(); }
            catch (Exception e) { System.out.println(e); }
            finally {
                isPlaying = false;
            }
        }).start();
    }

}
