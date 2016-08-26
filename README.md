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