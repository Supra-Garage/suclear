package tw.supra.suclear.utils.typedbox;

/**
 * 范型的回调抽象
 *
 * @param <MsgType> 消息类型
 * @author wangjia20
 * @since 2018-4-15
 */
public interface TypedCallback<MsgType> {

    /**
     * 回调时
     *
     * @param msg 消息
     */
    void onCallback(MsgType msg);
}
