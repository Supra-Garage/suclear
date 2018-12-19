package tw.supra.suclear.utils.typedbox;

/**
 * 范型的回调抽象
 *
 * @param <KeyType>   键类型
 * @param <ValueType> 值类型
 * @author wangjia20
 * @since 2018-4-15
 */
public interface TypedMapping<KeyType, ValueType> {

    /**
     * map 时
     *
     * @param k 键
     * @return 值
     */
    ValueType map(KeyType k);
}
