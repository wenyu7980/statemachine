## 状态机

作者：彣彧 wenyu7980@163.com

状态机相关介绍可以参考下面几篇文章

> [有限状态自动机概述](https://www.jianshu.com/p/01b5a98c5e9b)
>
> [状态机实战-监听](https://www.jianshu.com/p/75ca23800020)
>
> [有限状态自动机-守卫版](https://www.jianshu.com/p/97db3c14fb85)

##### maven
```xml
<!-- https://mvnrepository.com/artifact/com.wenyu7980/statemachine -->
<dependency>
    <groupId>com.wenyu7980</groupId>
    <artifactId>statemachine</artifactId>
    <version>{version}</version>
</dependency>
```
### 状态机库结构

#### 状态机

##### StateMachine

状态机核心类，负责迁移路径匹配和监听调用，状态数据的状态设定等。

调用顺序如下：

+ 状态机监听（开始）
+ 事件监听（前）
+ 状态设定
+ 事件监听（后）
+ 状态变化监听（离开）
+ 状态变化监听（进入）
+ 迁移路径匹配
+ 状态机监听（结束）



#### 状态机异常

##### StateMachineException 

状态机异常

> 状态机异常的抽象类

##### StateMachineNotFoundException

没有匹配的状态迁移路径异常

##### StateMachineTooManyException 

匹配出多条状态迁移路径异常

**注意**：如果出现改异常，说明状态机路径是有BUG，需要认真对待

#### 状态机迁移路径守卫

##### StateMachineGuard

根据上下文信息，判断是否允许通过该路径

> [有限状态自动机-守卫版](https://www.jianshu.com/p/97db3c14fb85)

#### 监听

##### StateMachineEventListener

事件监听

事件监听分为**触发前**和**触发后**两个监听，由**post**方法区分（false：前，true：后）

+ 触发前

  该监听主要目的是对上下文环境确认，例如是否允许是否满足执行条件；设置context用户路径守卫等。

  注意：尽量**不要**在改类型监听中**修改状态数据**

+ 触发后

  该监听主要目的是对状态数据进行更新。

  注意：修改状态由stateConsumer完成

##### StateMachineStateListener

状态变化监听

状态变化监听分为**离开状态**和**进入状态**两种监听，由**exit**方法区分（true：离开，false：进入）

+ 离开状态

  该监听主要目的是离开前确认是否满足离开条件,触发其他状态机

+ 进入状态

  该监听主要目的是通知操作者。

##### StateMachineTransformListener 

迁移监听

该监听主要目的是针对特定状态间的路径进行监听。

##### StateMachineListener 

状态机监听

事件监听分为**开始**和**结束**两个监听，由**start**（true：开始，false：结束）

该监听主要目的是打印日志。例如：状态机开始日志和结束日志。

#### 状态容器

##### StateContainer

单状态或复合状态的接口类
