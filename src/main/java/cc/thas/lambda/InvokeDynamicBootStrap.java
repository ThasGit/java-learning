package cc.thas.lambda;

import java.lang.invoke.*;

public class InvokeDynamicBootStrap {

    // 通过 invokeDynamic hello,(Void)Void 调用
    public static CallSite bootstrap(MethodHandles.Lookup caller,
                                     String invokedName,
                                     MethodType invokedType,
                                     String className) throws Throwable {
        // 定义 invokeDynamic 的方法签名, 方法名是传进来的 invokedName; 用于辅助classSite 生成方法句柄
        // 定义 invokeDynamic 的返回结果, 以及方法的入参
        // 我们把这个入参重定向到指定类的调用上, 实现对任意方法的调用
        MethodType targetMethodType = invokedType; //MethodType.methodType(String.class, Integer.TYPE);
        String targetMethodName = invokedName;

        // ABC 三个都继承自object, 只能转为object
        MethodHandle target = MethodHandles.publicLookup().findStatic(Class.forName(className), targetMethodName, targetMethodType);

        return new MutableCallSite(target);
    }

    public static CallSite bootstrap2(MethodHandles.Lookup caller,
                                      String invokedName,
                                      MethodType invokedType,
                                      String className, String type) throws Throwable {
        // 定义 invokeDynamic 的方法签名, 方法名是传进来的 invokedName; 用于辅助classSite 生成方法句柄
        // 定义 invokeDynamic 的返回结果, 以及方法的入参
        // 我们把这个入参重定向到指定类的调用上, 实现对任意方法的调用
        MethodType targetMethodType = invokedType; //MethodType.methodType(String.class, Integer.TYPE);
        String targetMethodName = invokedName;

        MethodHandle target;
        Class<?> clazz = Class.forName(className);
        if ("static".equals(type)) {
            target = MethodHandles.publicLookup().findStatic(clazz, targetMethodName, targetMethodType);
        } else if ("special".equals(type)) {
            // 权限问题搞不定
            throw new UnsupportedOperationException();

//            new MethodHandles.Lookup(String.class);
//            target = new MethodHandles.Lookup(String.class).findSpecial(clazz, targetMethodName, targetMethodType, clazz);
        } else {
            target = MethodHandles.publicLookup().findVirtual(clazz, targetMethodName, targetMethodType);
        }

        return new MutableCallSite(target);
    }

    public static void main(String[] args) throws Throwable {
        // invokeDynamic 不能用java去执行, 以下java代码模拟执行

        MethodType methodType = MethodType.methodType(String.class, Integer.TYPE);
        // invokeDynamic的第一个参数是bootstrap的前三个参数, 第二个参数是bootstrap的地址以及bootstrap的额外参数
        CallSite callSite = bootstrap(MethodHandles.lookup(), "hello", methodType, "cc.thas.dynamic.B");
        // iload_1
        int i = 1;
        Object result = callSite.getTarget().invoke(i);
        System.out.println("result:" + result);

        // 以上代码对于 invokeDynamic 而言, 只需要一行指令 加上所需的常量池常量 cp_info, 不需要像java代码一样需要很多中间变量

        // 再测试一个实例方法
        methodType = MethodType.methodType(Integer.TYPE);
        callSite = bootstrap2(MethodHandles.lookup(), "length", methodType, "java.lang.String", "virtual");
        String str = "12";
        System.out.println(callSite.getTarget().invoke(str));

    }
}
