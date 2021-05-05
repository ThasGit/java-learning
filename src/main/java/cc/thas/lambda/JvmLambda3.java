package cc.thas.lambda;

import java.lang.invoke.*;

public class JvmLambda3 {
    private String str = "1";

    /**
     * 引用了实例变量的lambda表达式
     */
    public void jvmLambda3() {
        MyFunctionInterface myFunctionInterface = (i) -> {
            System.out.println("jvmLambda");
            return String.valueOf(i + str);
        };
        // invokeDynamic 的作用就是生成函数式接口的匿名类的一个对象, 这个对象会调用 lambda 方法
        myFunctionInterface.testMethod(1);
    }

    /**
     * 引用了实例变量的 lambda 的 java 代码实现, 需要像非捕获lambda一样将实例对象作为首个参数传递进 get$lambda方法
     *
     * @throws Throwable
     */
    public void myLambda3() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // 用来生成匿名类的方法的methodType,
        MethodType makeAnonymousClassMethodType = MethodType.methodType(MyFunctionInterface.class, JvmLambda3.class);

        String functionalInterfaceName = "testMethod";
        // 生成的匿名类中的方法(get$lambda)的methodType/函数式接口的methodType
        MethodType functionalInterfaceMethodType = MethodType.methodType(String.class, Integer.TYPE);

        // 实际调用该匿名类方法时使用的methodType, 一般来说跟方法定义的methodType相同
        MethodType instantiatedMethodType = functionalInterfaceMethodType;

        // 非捕获式lambda methodType与函数式接口相同
        MethodType lambdaMethodType = functionalInterfaceMethodType;
        // lambda方法的方法句柄
        MethodHandle lambdaMethod = lookup.findVirtual(JvmLambda3.class, "lambda", lambdaMethodType);
        // callSite 对象只会生成一次
        CallSite callSite = LambdaMetafactory.metafactory(lookup,
                functionalInterfaceName, makeAnonymousClassMethodType,
                functionalInterfaceMethodType, lambdaMethod, instantiatedMethodType);
        // 生成匿名类也只会生成一次, 有缓存

        // 生成匿名类的对象
        MyFunctionInterface targetAnonymousClassObject = (MyFunctionInterface) callSite.getTarget().invoke(this);
        targetAnonymousClassObject.testMethod(1222);
        // 生成匿名类的对象
        Object target2 = callSite.getTarget().invoke(this);

        // 类是相同的
        System.out.println("class equals:" + (targetAnonymousClassObject.getClass() == target2.getClass()));
        // 非捕获式lambda代理对象也是相同的
        System.out.println("object equals:" + (targetAnonymousClassObject == target2));
    }

    /**
     * lambda 方法, 由于引用了实例变量, 所以是实例方法
     *
     * @param i lambda方法的参数
     * @return lambda方法的返回结果
     */
    public String lambda(int i) {
        System.out.println("myLambda");
        return String.valueOf(i + str);
    }

}
