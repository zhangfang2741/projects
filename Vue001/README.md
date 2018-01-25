基础结构：
        <div id="app"></div>
        var app=new Vue({
                el:'#app',
                data:{
                        
                },
                computed:{

                },
                methods:{

                }
                })
                js经典方法：
                //数字排序
                function sortNumber(a,b){
                        return a-b
                }
                //数组对象方法排序:
                function sortByKey(array,key){
                        return array.sort(function(a,b){
                                var x=a[key];
                                var y=b[key];
                                return ((x<y)?-1:((x>y)?1:0));
                        });
                }
服务器： npm install live-server -g

Vue 指令
        v-if v-else   用来判断是否加载html的DOM
        v-show  调整css中display属性，DOM已经加载，只是CSS控制没有显示出来
        v-for   v-for 指令需要以 (item,index) in items 形式的特殊语法，items 是源数据数组并且item是数组元素迭代的别名
        v-text  一般显示字符用的是{{xxx}},这种情况是有弊端的，就是当我们网速很慢或者javascript出错时，会暴露我们的{{xxx}}。Vue给我们提供的         v-text就能有效避免这个问题
        v-html  双大括号会将数据解释为纯文本，而非HTML。为了输出真正的HTML，你就需要使用v-html 指令，不过容易导致XSS攻击

        v-on    绑定的是事件，如click keyUp  等,缩写方法是@click   @keyUp.enter 
        v-model 绑定的是数据源  如textarea input type=checkbox|radio|text   等
                数据源绑定指的是存在输入时，则把输入的值传递给绑定的变量，变量改变时，又自动渲界面上的数据，所以叫做双向绑定。
        v-bind  绑定的是HTML中的标签属性的,如v-bind:src=""   缩写方法是:img
                绑定css和style样式时，可以绑定数组并进行判断，也可以使用三元表达式,style也可以。
                :class="[classA:isOk,isOk?classA:classB]" ,注意，这里面的变量都需要在data中注册
        v-pre   在模板中跳过vue的编译，直接输出原始值
        v-cloak 在vue渲染完指定的整个DOM后才进行显示。它必须和CSS样式一起使用
        style [v-cloak] {
                        display: none;
                        }
                html <div v-cloak>
                        {{ message }}
                </div>
        v-once  在第一次DOM时进行渲染，渲染完成后视为静态内容，跳出以后的渲染过程

Vue 函数：
        Vue.directive自定义指令：如  <span v-jspang="color"></span>
                注意，这里的color必须在data中定义，指令中出现的名称都需要在data中定义
                Vue.directive("指令名称",{
                        生命周期:钩子函数(el,binding,vnode){
                                处理
                        }
                        bind:只调用一次，指令第一次绑定到元素时调用，用这个钩子函数可以定义一个绑定时执行一次的初始化动作。
                        inserted:被绑定元素插入父节点时调用（父节点存在即可调用，不必存在于document中）。
                        update:被绑定于元素所在的模板更新时调用，而无论绑定值是否变化。通过比较更新前后的绑定值，可以忽略不必要的模板更新。
                        componentUpdated:被绑定元素所在模板完成一次更新周期时调用。
                        unbind:只调用一次，指令与元素解绑时调用。
                });
                Vue.directive("jspang",{
                bind:function(el,binding,vnode){//被绑定
                        el.style="color:"+binding.value;
                        console.log('1 - bind');
                },
                inserted:function(){//绑定到节点
                        console.log('2 - inserted');
                },
                update:function(){//组件更新
                        console.log('3 - update');
                },
                componentUpdated:function(){//组件更新完成
                        console.log('4 - componentUpdated');
                },
                unbind:function(){//解绑
                        console.log('5 - unbind');
                }
                });

        Vue.extend构造器的延伸
                当在模板中遇到该组件名称作为标签的自定义元素时，会自动调用“扩展实例构造器”来生产组件实例，并挂载到自定义元素上 <author></author>
                var authorExtend = Vue.extend({
                        template:"<p><a :href='authorUrl'>{{authorName}}</a></p>",
                        data:function(){
                        return{
                                authorName:'JSPang',
                                authorUrl:'http://www.jspang.com'
                                }
                        }
                        });
                这时html中的标签还是不起作用的，因为扩展实例构造器是需要挂载的，我们再进行一次挂载
                new authorExtend().$mount('author');

                还可以通过HTML标签上的id或者class来生成扩展实例构造器
                new authorExtend().$mount('#author');
                <span id="author"></span>
        Vue.set在构造器外部操作构造器内部的数据、属性或者方法
                什么是外部数据，就是不在Vue构造器里里的data处声明，而是在构造器外部声明，然后在data处引用就可以了
                //在构造器外部声明数据
                var outData={
                        count:1,
                        goodName:'car',
                        arr:['aaa','bbb','ccc']
                };
                var app=new Vue({
                        el:'#app',
                        //引用外部数据
                        data:outData
                })
                在外部改变数据的三种方法:
                        Vue.set(outData,'count',4);
                        app.count++;
                        outData.count++;
                为什么要有Vue.set的存在（数组限制）
                        由于Javascript的限制，Vue不能自动检测以下变动的数组。
                        *当你利用索引直接设置一个项时，vue不会为我们自动更新。
                        *当你修改数组的长度时，vue不会为我们自动更新。
                         //app.arr[1]='ddd';如果前面没有对虚拟节点的操作，如没有app.count++;操作，则无法自动更新
                         Vue.set(app.arr,1,'ddd');可以自动更新
        Vue的生命周期（钩子函数）
                Vue一共有10个生命周期函数，我们可以利用这些函数在vue的每个阶段都进行操作数据或者改变内容
                        //生命周期函数
                        beforeCreate:function(){
                                this.message++;
                                console.log('1-beforeCreate 创建之前');
                        },
                        created:function(){
                                
                                console.log('2-created 创建完成');
                        },
                        beforeMount:function(){
                                console.log('3-beforeMount 挂载之前');
                        },
                        mounted:function(){
                                console.log('4-mounted 被创建');
                        },
                        beforeUpdate:function(){
                                console.log('5-beforeUpdate 数据更新前');
                        },
                        updated:function(){
                                //this.message++;
                                console.log('6-updated 被更新后');
                        },
                        activated:function(){
                                console.log('7-activated');
                        },
                        deactivated:function(){
                                console.log('8-deactivated');
                        },
                        beforeDestroy:function(){
                                console.log('9-beforeDestroy 销毁之前');
                        },
                        destroyed:function(){
                                console.log('10-destroyed 销毁之后')
                        }
        Component 组件
                组件就是制作自定义的标签，这些标签在HTML中是没有的。比如：<jspang></jspang>,组件定义分为全局化注册组件和局部注册组件
                 //注册全局组件
                Vue.component('jspang',{
                         template:`<div style="color:red;">全局化注册的jspang标签</div>`
                })
                //注册局部组件
                var app=new Vue({
                        el:'#app',
                        components:{//这里多了个s
                                "panda":{
                                         template:`<div style="color:red;">局部注册的panda标签</div>`
                                }
                        }
                        })
                组件和指令的区别：组件注册的是一个标签，而指令注册的是已有标签里的一个属性
                props: 定义属性我们需要用props选项，加上数组形式的属性名称，例如：props:[‘here’]。在组件的模板里读出属性值只需要用插值的形式，例如{{ here }}
                 <panda :here="here" to-there="USA"></panda>
                 data:{
                        here:"China Sichuan"
                        },
                 components:{
                        "panda":{
                                template:`<div style="color:red;">熊猫来自{{here}},去{{toThere}}</div>`,
                                props:['here','toThere']
                        }
                }
                 属性偶尔会加入’-‘来进行分词，props里必须用小驼峰式写法props:['toThere']。
                 把构造器中data的值传递给组件，我们只要进行绑定就可以了。就是我们第一季学的v-bind:xxx.如:here
                 我们也可以在构造器外部写局部注册组件：
                        var jspang = {
                                template:`<div>Panda from China!</div>`
                                }
                                声明好对象后在构造器里引用就可以了。<jspang></jspang>
                                components:{
                                  "jspang":jspang
                                }
                 还可以进行父子组件的嵌套：
                <script type="text/javascript">
                        var city={
                                template:`<div>Sichuan of China</div>`
                        }
                        var jspang = {
                        template:`<div>
                                <p> Panda from China!</p>
                                <city></city>
                        </div>`,
                        components:{
                                "city":city
                        }
                        }
                        var app=new Vue({
                        el:'#app',
                        components:{
                                "jspang":jspang
                        }
                        
                        })
                 </script>
                 Component 标签:<component></component>标签是Vue框架自定义的标签，它的用途就是可以动态绑定我们的组件，根据数据的不同更换不同的组件。
                 <component v-bind:is="who"></component><!--is 是component定义的属性-->

        选    项（Option ）：
                propsData 不是和属性有关，他用在全局扩展时进行传递数据
                        var headerExtend=Vue.extend({
                                template:"<h2>这里是{{message}} 示例,由{{name}}创建</h2>",
                                data:function(){
                                        return {
                                                message:"propsData"
                                        }
                                },
                                props:['name']
                        });
                        new headerExtend({propsData:{name:'zhangfang'}}).$mount("header");
                computed：computed 的作用主要是对原数据进行改造输出。改造输出：包括格式的编辑，大小写转换，顺序重排，添加符号……。
                methods:methods中参数的传递,
                        methods中的$event参数:<button @click=”add(2,$event)”>add</button>,
                        native  给组件绑定构造器里的原生事件:在实际开发中经常需要把某个按钮封装成组件，然后反复使用，如何让组件调用构造器里的方法，而不是组件里的方法。就需要用到我们的.native修饰器了
                        <p><btn @click.native="add(3)"></btn></p> 调用的是构造器里面的方法
                watch：监控数据,示例：
                        watch:{
                                temperature:function(newVal,oldVal){
                                
                                }
                        }
                        有些时候我们会用实例属性的形式来写watch监控。也就是把我们watch卸载构造器的外部，这样的好处就是降低我们程序的耦合度，使程序变的灵活。
                        app.$watch('temperature',function(newVal,oldVal){});
                mixins 混入选项操作
                        Mixins一般有两种用途：
                        1、在你已经写好了构造器后，需要增加方法或者临时的活动时使用的方法，这时用混入会减少源代码的污染。
                        2、很多地方都会用到的公用方法，用混入的方法可以减少代码量，实现代码重用。
                        从执行的先后顺序来说，都是混入的先执行，然后构造器里的再执行，需要注意的是，这并不是方法的覆盖，而是被执行了两遍。当混入方法和构造器的方法重名时，混入的方法无法展现，也就是不起作用。
                        <script type="text/javascript">
                                //额外临时加入时，用于显示日志
                                var addLog={
                                        updated:function(){//钩子函数，实际上，这里在构造器里面出现的选项都可以在这里定义
                                                console.log("数据放生变化,变化成"+this.num+".");
                                        }
                                }
                                var app=new Vue({
                                el:'#app',
                                data:{
                                        num:1
                                },
                                methods:{
                                        add:function(){
                                                this.num++;
                                        }
                                },
                                mixins:[addLog]//混入
                                })
                        </script>
                        全局API混入方式：全局混入的执行顺序要前于混入和构造器里的方法。
                        Vue.mixin({
                                updated:function(){
                                        console.log('我是全局被混入的');
                                }
                                })
                extends  扩展选项 通过外部增加对象的形式，对构造器进行扩展
                            <script type="text/javascript">
                                var bbb={
                                created:function(){
                                        console.log("我是被扩展出来的");
                                },
                                methods:{
                                        add:function(){
                                        console.log('我是被扩展出来的方法！');
                                        }
                                }
                                };
                                var app=new Vue({
                                el:'#app',
                                data:{
                                        message:'hello Vue!'
                                },
                                methods:{
                                        add:function(){
                                        console.log('我是原生方法');
                                        }
                                },
                                extends:bbb
                                })
                        </script>
                delimiters 选项 delimiters的作用是改变我们插值的符号。Vue默认的插值是双大括号{{}}。但有时我们会有需求更改这个插值的形式
                        如：delimiters:['${','}'] 现在我们的插值形式就变成了${}。
        实例和内置组件
                Vue和Jquery.js一起使用
                        mounted:function(){
                                $('#app').html('我是jQuery!');
                        }
                实例调用自定义方法
                        methods:{
                                add:function(){
                                        console.log("调用了Add方法");
                                }
                        }
                        app.add();
                $mount（）：$mount方法是用来挂载我们的扩展的
                 var jspang = Vue.extend({
                        template:`<p>{{message}}</p>`,
                        data:function(){
                                return {
                                        message:'Hello ,I am JSPang'
                                        }
                        }
                })
                var vm = new jspang().$mount("#app")
                $destroy()：用$destroy()进行卸载。vm.$destroy();
                $forceUpdate() 更新方法。vm.$forceUpdate()
                $nextTick() 数据修改方法，当Vue构造器里的data值被修改完成后会调用这个方法，也相当于一个钩子函数吧，和构造器里的updated生命周期很像。
                        function tick(){
                                vm.message="update message info ";
                                vm.$nextTick(function(){
                                        console.log('message更新完后我被调用了');
                                })
                        }
                安装Vue的控制台调试工具
                实例事件：在构造器外部写一个调用构造器内部的方法。这样写的好处是可以通过这种写法在构造器外部调用构造器内部的数据
                        app.$on('reduce',function(){//监听实例事件的调用
                                        console.log('执行了reduce()');
                                        this.num--;
                                });
                $once执行一次的事件。
                        app.$once('reduceOnce',function(){
                                console.log('只执行一次的方法');
                                this.num--;
                                });
                $off关闭事件
                        //关闭事件
                        function off(){
                                app.$off('reduce');
                        }

vue-cli学习：
                一、安装vue-cli:
                        安装vue-cli的前提是你已经安装了npm，安装npm你可以直接下载node的安装包进行安装。你可以在命令行工具里输入
                        npm -v  检测你是否安装了npm和版本情况,npm没有问题，接下来我们可以用npm 命令安装vue-cli了，在命令行输入下面的命令：
                        npm install vue-cli -g  如果vue -V的命令管用了，说明已经顺利的把vue-cli安装到我们的计算机里了。
                二、初始化项目
                         vue init <template-name> <project-name>
                                init：表示我要用vue-cli来初始化项目
                                <template-name>:表示模板名称，vue-cli官方为我们提供了5种模板，
                                                webpack-一个全面的webpack+vue-loader的模板，功能包括热加载，linting,检测和CSS扩展。
                                                webpack-simple-一个简单webpack+vue-loader的模板，不包含其他功能，让你快速的搭建vue的开发环境。
                                                browserify-一个全面的Browserify+vueify 的模板，功能包括热加载，linting,单元检测。
                                                browserify-simple-一个简单Browserify+vueify的模板，不包含其他功能，让你快速的搭建vue的开发环境。
                                                simple-一个最简单的单页应用模板。
                                <project-name>：标识项目名称，这个你可以根据自己的项目来起名字。
                         在实际开发中，一般我们都会使用webpack这个模板，那我们这里也安装这个模板，在命令行输入以下命令：
                         vue init webpack vuecliTest
                                1、cd vuecliTest  进入我们的vue项目目录。
                                2、npm install  安装我们的项目依赖包，也就是安装package.json里的包，如果你网速不好，你也可以使用cnpm来安装。
                                3、npm run dev 开发模式下运行我们的程序。给我们自动构建了开发用的服务器环境和在浏览器中打开，并实时监视我们的代码更改，即时呈现给我们。
                                这里列出的是模板为webpack的目录结构:
                                |-- build                            // 项目构建(webpack)相关代码
                                |   |-- build.js                     // 生产环境构建代码
                                |   |-- check-version.js             // 检查node、npm等版本
                                |   |-- dev-client.js                // 热重载相关
                                |   |-- dev-server.js                // 构建本地服务器
                                |   |-- utils.js                     // 构建工具相关
                                |   |-- webpack.base.conf.js         // webpack基础配置
                                |   |-- webpack.dev.conf.js          // webpack开发环境配置
                                |   |-- webpack.prod.conf.js         // webpack生产环境配置
                                |-- config                           // 项目开发环境配置
                                |   |-- dev.env.js                   // 开发环境变量
                                |   |-- index.js                     // 项目一些配置变量
                                |   |-- prod.env.js                  // 生产环境变量
                                |   |-- test.env.js                  // 测试环境变量
                                |-- src                              // 源码目录
                                |   |-- components                     // vue公共组件
                                |   |-- store                          // vuex的状态管理
                                |   |-- App.vue                        // 页面入口文件
                                |   |-- main.js                        // 程序入口文件，加载各种公共组件
                                |-- static                           // 静态文件，比如一些图片，json数据等
                                |   |-- data                           // 群聊分析得到的数据用于数据可视化
                                |-- .babelrc                         // ES6语法编译配置
                                |-- .editorconfig                    // 定义代码格式
                                |-- .gitignore                       // git上传需要忽略的文件格式
                                |-- README.md                        // 项目说明
                                |-- favicon.ico 
                                |-- index.html                       // 入口页面
                                |-- package.json                     // 项目基本信息
                                package.json
                        package.json文件是项目根目录下的一个文件，定义该项目开发所需要的各种模块以及一些项目配置信息（如项目名称、版本、描述、作者等）。package.json 里的scripts字段，这个字段定义了你可以用npm运行的命令。在开发环境下，在命令行工具中运行npm run dev 就相当于执行 node build/dev-server.js  .也就是开启了一个node写的开发行建议服务器。由此可以看出script字段是用来指定npm相关命令的缩写
                        "scripts": {
                                "dev": "node build/dev-server.js",
                                "build": "node build/build.js"
                        }
                        dependencies字段和devDependencies字段
                                dependencies字段指项目运行时所依赖的模块；
                                devDependencies字段指定了项目开发时所依赖的模块；
                                在命令行中运行npm install命令，会自动安装dependencies和devDempendencies字段中的模块
                        webpack配置相关
                        我们在上面说了运行npm run dev 就相当于执行了node build/dev-server.js,说明这个文件相当重要，先来熟悉一下它。

                        如果要在.vue單頁面模板中插入第三方模板，可以采用以下方式：
                        <script>
                                import Hi2 from '@/components/Hi.2'
                                export default {
                                        name: "hi",
                                        data: function() {
                                         return {
                                                msg: "Hi I am JSPANG"
                                                };
                                        },
                                        components: {"Hi2":Hi2}
                                };
                        </script>

Vue-router入门
                 Vue作的都是单页应用，就相当于只有一个主的index.html页面,必须使用vue-router来进行管理
                 安装：
                 npm install vue-router --save-dev

                 vue-cli生成的默认模板中，src/main.js主要文件，内容如下：
                        new Vue({
                                el: '#app',
                                router,
                                components: { App },
                                template: '<App/>'
                        })
                        该文件可以这样解释：
                        APP是主页模板，router是导航配置
                        首先构建一个vue实例挂载到app标签上，内容就是<App/>组件中的内容，当请求到来时，从router路由中寻找对应的组件，并且显示到APP组件中的 <router-view/>中。APP组件在这里相当于一个拦截器，所有的请求都需要经过APP进行显示。

                 我们用vue-cli生产了我们的项目结构，在src/router/index.js文件，这个文件就是路由的核心文件。
                        import Vue from 'vue'   //引入Vue
                        import Router from 'vue-router'  //引入vue-router
                        import Hello from '@/components/Hello'  //引入根目录下的Hello.vue组件
                        Vue.use(Router)  //Vue全局使用Router
                        export default new Router({
                                routes: [              //配置路由，这里是个数组
                                        {                    //每一个链接都是一个对象
                                                path: '/',         //链接路径
                                                name: 'Hello',     //路由名称，
                                                component: Hello   //对应的组件模板
                                        }
                                ]
                        })
                router-link制作导航
                        <p>导航:
                                <router-link to="/">首页</router-link>
                                <router-link to="/hi">Hi页面</router-link>
                        </p>
                        <router-view/>

                vue-router配置子路由
                        子路由的情况一般用在一个页面有他的基础模版，然后它下面的页面都隶属于这个模版，只是部分改变样式。
                        页面搭建：
                            hi1.vue  hi2.vue      
                     
                        父模板修改：
                        <p>导航 ：
                                <router-link to="/">首页</router-link> | 
                                <router-link to="/hi">Hi页面</router-link> |
                                <router-link to="/hi/hi1">-Hi页面1</router-link> |
                                <router-link to="/hi/hi2">-Hi页面2</router-link>
                        </p>
                        <router-view/>
                        路由修改：
                        component:Hi,
                        children:[
                                {path:'/',component:Hi},
                                {path:'hi1',component:Hi1},
                                {path:'hi2',component:Hi2},
                        ]

                        配置路由都要经历页面搭建、父模板修改、路由修改三个步骤
                {{ $route.name}}可以显示路由器中配置的地址名称，但是如果有子路由，则名称不显示，只显示子路由叶子的名称。
                         如：
                          {
                                path:'/hi',
                                name:'hi',
                                component:Hi,
                                children:[
                                        {path:'/',component:Hi},
                                        {path:'hi1',component:Hi1,name:'Hi1'},
                                        {path:'hi2',component:Hi2,name:'Hi2'}
                                ]
                        }
                        这里访问/hi时，不显示hi的名称：
                        {path:'/',component:Hi,name:"HelloWorld/hi"},
                        {path:'hi1',component:Hi1,name:'HelloWorld/hi/Hi1'},
                        {path:'hi2',component:Hi2,name:'HelloWorld/hi/Hi2'}//可以做面包屑

                vue-router参数传递
                        <router-link :to="{name:"路由中定义的名字，如hi",params:{key:value}}">valueString</router-link>
                        如： 
                        <router-link :to="{name:'Hi1',params:{username:'jspang'}}">Hi页面</router-link>
                        在name为Hi1的组件中可以获取传递的参数：
                        {{$route.params.username}}获取传递的参数。
                vue-router 利用url传递参数
                        :冒号的形式传递参数
                        {
                                path:'/params/:newsId/:newsTitle',
                                component:Params
                        }
                        在Params頁面可以拿到数据：
                        <template>
                                <div>
                                        <h2>{{ msg }}</h2>
                                        <p>新闻ID：{{ $route.params.newsId}}</p>
                                        <p>新闻标题：{{ $route.params.newsTitle}}</p>
                                </div>
                        </template>
                单页面多路由区域操作
                        在一个页面里我们有2个以上<router-view>区域，如：
                        <router-view ></router-view>
                        <router-view name="left" style="float:left;width:50%;background-color:#ccc;height:300px;"></router-view>
                        <router-view name="right" style="float:right;width:50%;background-color:#c0c;height:300px;"></router-view>
                        Vue.use(Router)
                        router配置则应当如下：
                        export default new Router({
                        routes: [
                                {
                                path: '/',
                                components: {
                                        default:Hello,
                                        left:Hi1,
                                        right:Hi2
                                }
                                },{
                                path: '/Hi',
                                components: {
                                        default:Hello,
                                        left:Hi2,
                                        right:Hi1
                                }
                                }
                        ]
                        })
                redirect基本重定向
                        开发中有时候我们虽然设置的路径不一致，但是我们希望跳转到同一个页面，或者说是打开同一个组件。这时候我们就用到了路由的重新定向redirect参数。
                        把原来的component换成redirect参数就可以了
                        export default new Router({
                                routes: [
                                        {
                                                path: '/',
                                                component: Hello
                                        },{
                                                path:'/params/:newsId(\\d+)/:newsTitle',
                                                component:Params
                                        },{
                                                path:'/goback',
                                                redirect:'/'
                                        }
                                ]
                         })
                重定向时传递参数:
                        {
                                path:'/params/:newsId(\\d+)/:newsTitle',
                                component:Params
                        },{
                                path:'/goParams/:newsId(\\d+)/:newsTitle',
                                redirect:'/params/:newsId(\\d+)/:newsTitle'
                        }
                alias别名的使用
                        {
                                path: '/hi1',
                                component: Hi1,
                                alias:'/jspang'
                        }//别名请不要用在path为’/’中
                路由的过渡动画
                        <transition name="fade">
                                <router-view ></router-view>
                        </transition>
                写在模板中的钩子函数,监控到路由的进入和路由的离开，也可以轻易的读出to和from的值。:訪問path:'/params/:newsId(\\d+)/:newsTitle',
                        beforeRouteEnter：在路由进入前的钩子函数。
                        beforeRouteLeave：在路由离开前的钩子函数。
                        
                        export default {
                                        name: 'params',
                                        data () {
                                        return {
                                        msg: 'params page'
                                }
                                },
                                        beforeRouteEnter:(to,from,next)=>{
                                        console.log("准备进入路由模板");
                                        next();
                                },
                                        beforeRouteLeave: (to, from, next) => {
                                        console.log("准备离开路由模板");
                                        next();
                                }
                        }
                        </script>
                编程式导航，顾名思义，就是在业务逻辑代码中实现导航。
                 his.$router.go(-1) 和 this.$router.go(1)
                        这两个编程式导航的意思是后退和前进
                this.$router.push(‘/xxx ‘) 这个编程式导航都作用就是跳转

vuex            引入Vuex
                        npm install vuex --save  要注意的是这里一定要加上 –save，因为你这个包我们在生产环境中是要使用的。
                        新建一个store文件夹（这个不是必须的），并在文件夹下新建store.js文件，文件中引入我们的vue和vuex。
                                import Vue from 'vue';
                                import Vuex from 'vuex';
                                Vue.use(Vuex);
                                export default new Vuex.Store({

                                });
                        vuex就算引用成功

                        在main.js 中引入上面定义的store文件
                        import store from './vuex/store'
                        再然后 , 在实例化 Vue对象时加入 store 对象 :
                          new Vue({
                                el: '#app',
                                router,
                                store,//使用store
                                template: '<App/>',
                                components: { App }
                                })
                Vuex.Store主要有state、mutations、getters、actions、modules五大配置
                store.js
                export default new Vuex.Store({
                        state: {//这个就是我们说的访问状态对象，它就是我们SPA（单页应用程序）中的共享值。
                                count: 1,
                                arr: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                        },
                        mutations: {
                                add(state, n) {
                                        state.count += n;
                                },
                                reduce(state) {
                                        state.count -= 1;
                                }
                        },
                        getters: {
                                count: function (state) {
                                        return state.count += 100;
                                }
                        },
                        actions: {
                                addAction(context) {
                                        context.commit('add', 10)
                                },
                                reduceAction({commit}) {
                                        _setInterval(function () {
                                        commit("reduce")
                                        }, 1000);
                                        console.log('我比reduce提前执行');
                                }
                        }
                        });
                //Count.vue组件
                <template>
                <div>
                        <h2>{{msg}}</h2>
                        <hr/>
                        <h3>{{count}}</h3>
                        <h3>{{arr}}</h3>
                        <div>
                        <button type="button" class="btn btn-primary" @click="$store.commit('add',2)">+</button>//原生态调用方式
                        <button type="button" class="btn btn-primary" @click="$store.commit('reduce')">-</button>
                        <p>Mutations同步方法：
                                <button @click="add(2)">+</button>
                                <button @click="reduce">-</button>
                        </p>
                        
                        <p>actions:异步方法：
                                <button @click="addAction">+</button>
                                <button @click="reduceAction">-</button>
                        </p>
                        </div>
                </div>
                </template>
                <script>
                // import store from "@/vuex/store";
                import { mapState, mapMutations, mapGetters, mapActions} from "vuex";//应用store中提供的对象
                export default {
                data() {
                return {
                msg: "Hello Vuex"
                };
                },
                // store,
                //   computed: {
                //     count() {
                //       return this.$store.state.count;
                //     }
                //   }
                // computed:mapState({
                //         count:state=>state.count  //理解为传入state对象，修改state.count属性
                //      })

                computed: {//调用State和Getters
                        ...mapState(["count", "arr"]),
                        ...mapGetters(["count"])
                },
                methods: {//Mutations同步方法和Actions异步方法
                        ...mapMutations(["add", "reduce"]),
                        ...mapActions(["addAction", "reduceAction"])
                }
                };
                </script>

Axios           安装：使用 npm:
                        $ npm install axios  --save
                使用：
                发送一个 GET 请求
                axios.get('/user?ID=12345')
                        .then(function (response) {
                                console.log(response);
                        })
                        .catch(function (response) {
                                console.log(response);
                        }); 
                发送一个 POST 请求
                axios.post('/user', {
                                firstName: 'Fred',
                                lastName: 'Flintstone'
                        })
                        .then(function (response) {
                                console.log(response);
                        })
                        .catch(function (response) {
                                console.log(response);
                        });  
                发送多个并发请求
                axios.all([()=>axios.get('/user/12345'), ()=>axios.get('/user/12345/permissions'))])
                .then(axios.spread(function (acct, perms) {
                        // Both requests are now complete
                }));                                    

VUE插件开发：        







                        
                



