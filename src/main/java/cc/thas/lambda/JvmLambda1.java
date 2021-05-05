package cc.thas.lambda;

import java.lang.invoke.*;

public class JvmLambda1 {

    /**
     * 非捕获式的lambda表达式
     */
    public static void jvmLambda() {
        MyFunctionInterface myFunctionInterface = (i) -> {
            System.out.println("jvmLambda");
            return String.valueOf(i);
        };
        // invokeDynamic 的作用就是生成函数式接口的匿名类的一个对象, 这个对象会调用 lambda 方法
        myFunctionInterface.testMethod(1);
    }

    /**
     * 非捕获的 lambda 的 java 代码实现
     *
     * @throws Throwable
     */
    public static void myLambda() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // 用来生成匿名类的方法的methodType
        MethodType makeAnonymousClassMethodType = MethodType.methodType(MyFunctionInterface.class);

        String functionalInterfaceName = "testMethod";
        // 生成的匿名类中的方法(get$lambda)的methodType/函数式接口的methodType
        MethodType functionalInterfaceMethodType = MethodType.methodType(String.class, Integer.TYPE);

        // 实际调用该匿名类方法时使用的methodType, 一般来说跟方法定义的methodType相同
        MethodType instantiatedMethodType = functionalInterfaceMethodType;

        // 非捕获式lambda methodType与函数式接口相同
        MethodType lambdaMethodType = functionalInterfaceMethodType;
        // lambda方法的方法句柄
        MethodHandle lambdaMethod = lookup.findStatic(JvmLambda1.class, "lambda", lambdaMethodType);
        // callSite 对象只会生成一次
        CallSite callSite = LambdaMetafactory.metafactory(lookup,
                functionalInterfaceName, makeAnonymousClassMethodType,
                functionalInterfaceMethodType, lambdaMethod, instantiatedMethodType);
        // 生成匿名类也只会生成一次, 有缓存

        // 生成匿名类的对象
        MyFunctionInterface targetAnonymousClassObject = (MyFunctionInterface) callSite.getTarget().invoke();
        targetAnonymousClassObject.testMethod(1222);
        // 生成匿名类的对象
        Object target2 = callSite.getTarget().invoke();

        // 类是相同的
        System.out.println("class equals:" + (targetAnonymousClassObject.getClass() == target2.getClass()));
        // 非捕获式lambda代理对象也是相同的
        System.out.println("object equals:" + (targetAnonymousClassObject == target2));
    }

    /**
     * lambda 方法
     *
     * @param i lambda方法的参数
     * @return lambda方法的返回结果
     */
    public static String lambda(int i) {
        System.out.println("myLambda");
        return String.valueOf(i);
    }

}
