package com.caesar;

import com.caesar.tool.BeanConverterTools;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class BeanConverterToolsTest {

    public static void main(String[] args) {
        Source source = new Source("Alice", 30);

        // 转换单个对象
        Target target1 = BeanConverterTools.convert(source, Target.class);
        System.out.println(target1.getName() + " - " + target1.getAge());

        // 直接从类类型转换单个对象
        Target target2 = BeanConverterTools.convert(Source.class, Target.class);
        System.out.println(target2.getName() + " - " + target2.getAge());

        // 转换对象列表
        List<Source> sourceList = Arrays.asList(new Source("Bob", 25), new Source("Charlie", 35));
        List<Target> targetList = BeanConverterTools.convertList(sourceList, Target.class);
        targetList.forEach(target -> System.out.println(target.getName() + " - " + target.getAge()));
    }


    public static class Source {
        private String name;
        private int age;

        // 无参构造
        public Source() {}

        // 有参构造
        public Source(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        // getters and setters
    }

    public static class Target {
        private String name;
        private int age;

        // 无参构造
        public Target() {}

        // 有参构造
        public Target(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
// getters and setters
    }


}
