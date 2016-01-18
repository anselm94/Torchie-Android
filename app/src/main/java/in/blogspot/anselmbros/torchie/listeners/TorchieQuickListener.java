package in.blogspot.anselmbros.torchie.listeners;

/**
 * Created by anselm94 on 4/1/16.
 */
public interface TorchieQuickListener {
    void onFlashStateChanged(boolean enabled);

    void onFlashError(String error);
}
