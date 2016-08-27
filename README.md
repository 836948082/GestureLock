# GestureLock
# 手势密码解锁

 *        携带手势密码监听
 * 		  BaseActivity extends BaseFragmentActivity(所有继承BaseActivity的页面都将携带手势密码监听)
 * 		   不携带手势密码监听
 * 		  BaseActivity extends FragmentActivity(所有继承BaseActivity的页面都不携带手势密码监听)

现所有页面继承 BaseActivity 而BaseActivity又继承 BaseFragmentActivity
方便在 BaseActivity 中更改是否启用手势密码<监听>功能   ***   (这部分暂时没用到)

1.修复不正常退出后重启程序时出现两边验证密码
2.修复在修改页面和忘记密码页面退回到页面在进入程序后触发返回键进入到程序漏洞
3.修复继承 BaseActivity 页面中直接去掉Title效果

# 修复完善

4.修复倒计时TIME后触发屏幕循环多次出现验证密码BUG
5.修复进入程序后自动开始倒计时(在没有触发屏幕的情况下)
6.修复倒计时自动锁屏时忘记密码页面返回键进入程序BUG
7.增加BaseActivity的屏幕触发事件
