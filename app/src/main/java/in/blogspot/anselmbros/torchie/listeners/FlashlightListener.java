package in.blogspot.anselmbros.torchie.listeners;

/**
 * Created by anselm94 on 2/12/15.
 */
public interface FlashlightListener {
    void onFlashStateChanged(boolean enabled);

    void onFlashError(String error);
}
