package media.callshow.vc.flash.event;

public class HEvent {

    public final Object[] message;

    public HEvent(Object... message) {
        this.message = message;
    }

    public Object[] getMessage() {
        return message;
    }
}
