package in.blogspot.anselmbros.torchie.listeners;

/**
 * Created by anselm94 on 2/12/15.
 */
public interface FlashListener {
    void onFlashStateChanged(boolean enabled);

    void onFlashError(String error);
}
