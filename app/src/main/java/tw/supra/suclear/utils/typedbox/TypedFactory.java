package tw.supra.suclear.utils.typedbox;

/**
 * 范型的抽象工厂
 *
 * @param <OuT> Out Type 产出类型
 *
 * @author wangjia20
 * @since 2018-09-20
 */
public interface TypedFactory<OuT> {

    /**
     * 产出
     *
     * @return 产出
     */
    OuT create();
}
