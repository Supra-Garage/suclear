package tw.supra.suclear.utils.typedbox;

/**
 * 范型的解析器抽象
 *
 * @param <InT> In Type
 * @param <OuT> Out Type
 * @author wangjia20
 * @since 2018-4-15
 */
public interface TypedParser<InT, OuT, ExT extends Exception> {
    /**
     * 解析
     *
     * @param in 输入
     * @return out 输出
     */
    OuT parse(InT in) throws ExT;
}
