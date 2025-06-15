package cfg;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import handler.ivstAcc.dto.QueryDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Path;
// org.springframework.web.bind.annotation.*;
//import org.thymeleaf.context.Context;
// org.thymeleaf.context.Context;
import model.other.ContentType;
import orgx.uti.context.ProcessContext;
import util.annos.Paths;
import util.model.Context;
import util.rest.RestUti;
import util.serverless.ApiGateway;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;


// static ztest.htmlTppltl.rend;
import static cfg.Containr.sessionFactory;
import static orgx.uti.http.RegMap.registerMapping;
import static orgx.uti.http.RegMap.registerMappingHttpHdlr;
import static util.algo.JarClassScanner.getPrjPath;
import static util.algo.JarClassScanner.getTargetPath;
import static util.misc.PathUtil.getDirTaget;
import static util.misc.Util2025.encodeJson;
import static util.misc.util2026.*;
import static util.rest.RestUti.*;

/**
 * ini web   \n
 * ini url-->bean.handler()  \n
 * ini containr
 */
public class WebSvr {

    HttpServer httpServer;
    public static void startWebSrv() throws Exception {
        int port = 8889;
        if( new File("/port888").exists())
            port = 888;


        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        ProcessContext.httpServer = httpServer;

        registerMappingSomepath(httpServer);
        httpServer.createContext("/apiv1/res/uploads", new StaticFileHandler(getPrjPath() + "/res/uploads", "/apiv1/res/uploads"));

        //------------hdl all
        HttpHandler httpHandlerAll = exchange -> {

            try {
                regMapOptionn(exchange) ;
                handleAllReq(exchange);
            }catch (BreakToRet e){
                return;
            }
            catch (Exception e) {
               throwEx(e);
            }

        };
        String path = "/";
        registerMappingHttpHdlr(path,httpHandlerAll);

        //-------------------
        registerMappingSomepath2(httpServer);
        //  http://localhost:8889/
        // 启动服务器...
        //   httpServer.setExecutor(null); // 默认的线程池  单线程
        //每个请求新开一个线程）：


        setExecutorNewThrdAlwys(httpServer);
        //  httpServer.setExecutor(Executors.newSingleThreadExecutor());//每次新线程

        httpServer.start();
        System.out.println("http://localhost:" + port + "/reg");
        System.out.println("Server started on port " + port);
    }




    private static void registerMappingSomepath(HttpServer httpServer) throws URISyntaxException {
        // 定义一个上下文，绑定到 "/api/hello" 路径
        //  httpServer.createContext("/hello", new HelloHandler());


        // 设置静态资源目录 (例如: D:/myweb/static)
        //  String dirWzClassesDirSameLev = "static";
        String docRestApiDir = getdirRestapiDoc();
        System.out.println("docRestApiDir=" + docRestApiDir);

//        httpServer.createContext("/static22", new StaticFileHandler(docRestApiDir + "/aa", "/static"));
        httpServer.createContext("/static", new StaticFileHandler(docRestApiDir, "/static"));
        httpServer.createContext("/docRestApi", new StaticFileHandler(docRestApiDir, "/docRestApi"));


        //    http://localhost:8889/static/doc.htm
        httpServer.createContext("/post2", exchange -> {
            try {
                handlePost2(exchange);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        httpServer.createContext("/jar/docRestApi", exchange -> docRestApiHdl(exchange));

        httpServer.createContext("/favicon.ico",(exchange) -> {
            exchange.close();
            return;
        });
    }

    // 本地目录作为共享根目录
    //  wbdvStart();
    // 监听 8080 端口..
//        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//        System.out.println("WebDAV server started on http://localhost:8080/");
//
//        // 使用 Milton 处理请求
//        String webroot1 = "webroot";
//        HttpManager milton = new HttpManager(
//                new FileSystemResourceFactory(new File(webroot1), null, null),
//                new WebDavProtocol(new LockManagerImpl()),
//                new Http11Protocol()
//        );
//
//        // 所有请求交给 Milton 处理
//        server.createContext("/wbdv", exchange -> {
//            try {
//                milton.process(new MiltonHttpExchangeAdapter(exchange));
//            } catch (Throwable e) {
//                e.printStackTrace();
//                exchange.sendResponseHeaders(500, -1);
//            } finally {
//                exchange.close();
//            }
//        });

    private static void wbdvStart() throws IOException {
//        File rootDir = new File("webroot");
//        if (!rootDir.exists()) rootDir.mkdirs();
//
//        // 创建 Milton 的资源工厂（从文件系统映射资源）
//        ResourceFactory resourceFactory = new FileSystemResourceFactory(
//                new File(rootDir.getAbsolutePath()),
//                null, // no security manager
//                null  // no contentService
//        );
//
//        // Milton 响应处理器
//        WebDavResponseHandler responseHandler = new DefaultWebDavResponseHandler(resourceFactory);
//
//        // Milton HTTP 管理器（请求分发核心）
//        HttpManager httpManager = new HttpManager(resourceFactory, responseHandler,
//                new WebDavProtocol(), new Http11Protocol());
//
//        // 启动 Java 原生 HttpServer
//        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//        server.createContext("/", exchange -> {
//            try {
//                // Create and use Milton adapter for the request
//                httpManager.process(new wbdv.MiltonExchangeAdapter(exchange),
//                new MiltonResponseAdapter(exchange)
//                );
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                exchange.sendResponseHeaders(500, -1);
//            } finally {
//                exchange.close();
//            }
//        });
    }

    //parse /jar/restdoc
    private static void docRestApiHdl(HttpExchange exchange) throws IOException {
        try {


        URI uri = exchange.getRequestURI();
        String path = uri.getPath();  //   /jar/api  noqrystr
        String jarPath = path;
        jarPath = jarPath.substring(4);
        String txt = getTxtFrmJar(jarPath);

        wrtResp(exchange, txt, ContentType.TEXT_HTML.getValue());
     } finally {
        exchange.close(); // ⚠️ 必须调用，否则会卡住
        }
    }

    /**
     * 读取jar里面的某个文本文件资源
     *
     * @param pathInJar
     * @return
     */
    private static @NotNull String getTxtFrmJar(@NotBlank String pathInJar) {
        try (InputStream in = WebSvr.class.getResourceAsStream(pathInJar)) {
            if (in == null) {
                System.err.println("资源未找到: " + pathInJar);
                return "";
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }

        } catch (Exception e) {
            System.err.println("读取资源失败: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private static void handlePost2(HttpExchange exchange) throws Exception {
        try{
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                // body:::     a=1&b=2

                // 读取请求体
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);


                // 解析 form 数据
                Map<String, String> params = parseFormData(body);
                String response = "Received: a=" + params.get("a") + ", b=" + params.get("b");

                // 发送响应
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // 方法不允许
            }
        }  finally {
        exchange.close(); // ⚠️ 必须调用，否则会卡住
    }

    }

    private static Map<String, String> parseFormData(String body) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(URLDecoder.decode(keyValue[0], "UTF-8"),
                        URLDecoder.decode(keyValue[1], "UTF-8"));
            }
        }
        return params;
    }

    @org.jetbrains.annotations.NotNull
    private static String getdirRestapiDoc() throws URISyntaxException {
        String dirTaget = getDirTaget();
        String staticDir = "C:\\Users\\attil\\IdeaProjects\\jvprj2025\\static";

        String docRestDir = "/docRestApi/";
        String prjDirMode = getPrjPath() + docRestDir;
        // if(new File("/staticSrc").exists())
        //     staticDir = "C:\\0prj\\jvprj2025\\static";
        String targetDirMode = getTargetPath() + docRestDir;
        if (isExistDir(prjDirMode)) {
            staticDir = prjDirMode;
        } else if (isExistDir(targetDirMode)) {
            staticDir = prjDirMode;
        } else {
            dirTaget = getDirTaget();
            staticDir = dirTaget + docRestDir;
        }
        return staticDir;
    }

    private static boolean isExistDir(String prjDirMode) {
        return new File(prjDirMode).exists();
    }


//    http://localhost:8889/QueryOrdChrgHdr

    //    MutablePicoContainer container = IocPicoCfg.iniIocContainr();
//             AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);


    // path,hdrClz
    public static Map<String, Class> pathClzMap = new HashMap<>();

    /**
     * ini rest url wz bean
     *
     * @param server
     */
    public static void registerMappingSomepath2(HttpServer server) {

//        server.createContext("/users/get", exchange -> handleGetUser(exchange));
//
//
//        String path = "/tt2";
//        server.createContext(
//                path, exchange -> handleGetUser(exchange));


        //   /p8?uname=123
        createContext4rest("/p8", QueryDto.class, dto1 -> hdlDto2(dto1), server);

        RestUti.httpSvr=server;
        Context ctx = new Context();
        ctx.sessionFactory=sessionFactory;
        RestUti.contextThdloc.set(ctx);
        createContext4rest("/p9", QueryDto.class, WebSvr::hdl9);
        createContext4rest("/p99",  WebSvr::hdl99);

    }
    private static Object hdl9(QueryDto dto1) {
        System.out.println("fun hdl9(prm=" + encodeJson(dto1));
        return 9;
    }
    private static Object hdl99() {
        Context ctx=RestUti.contextThdloc.get();
        System.out.println("fun hdl8()");
        return 99;
    }





    /**
     *
     * @param dto1
     * @return   apigatewayResponse
     */
    private static Object hdlDto2(QueryDto dto1) {
        System.out.println("fun hdldto(prm=" + encodeJson(dto1));
        return 888;
    }


    public static void iniRestPathMap() {
        Consumer<Class> fun = aClass -> {
            if (aClass.getName().startsWith("api") || aClass.getName().startsWith("handler")) {
                //  var bean=getBeanFrmSpr(aClass);
                var path = getPathFromBean(aClass);
                System.out.println("pathMap(path=" + path + ",aClass=" + aClass.toString());
                //   server.createContext(path, (HttpHandler) bean);
                pathClzMap.put(path, aClass);
                if (aClass.getName().contains("RechargeHdr"))
                    System.out.println("D835");
                var path_pkgNclsname = getAutoRouterPath(aClass);
                pathClzMap.put(path_pkgNclsname, aClass);
                System.out.println("pathMap(path=" + path_pkgNclsname + ",aClass=" + aClass.toString());

                String[] getPathsFromBeanRzt = getPathsFromBean(aClass);
                for (String p : getPathsFromBeanRzt) {

                    pathClzMap.put(p, aClass);
                    System.out.println("pathMap(path=" + p + ",aClass=" + aClass.toString());

                }
            }
        };
        System.out.println("====start createContext");
        scanAllClass(fun);
        System.out.println("====end createContext");
    }

    /**
     * 获取自动化的路由路径，规则: 上一级包名/类名
     * 例如  /role/SaveRoleHdl
     *
     * @param aClass 要处理的类
     * @return 自动生成的路由路径
     */
    public static String getAutoRouterPath(Class<?> aClass) {
        if (aClass == null) {
            return "";
        }

        Package pkg = aClass.getPackage();
        String packageName = pkg != null ? pkg.getName() : "";
        String[] parts = packageName.split("\\.");
        String lastPkg = parts.length > 0 ? parts[parts.length - 1] : "";
        return "/" + lastPkg + "/" + aClass.getSimpleName();
    }


    /**片段是一个 HTTP 请求拦截器，用于拦截所有请求，包括：

     处理 OPTIONS 请求（跨域预检）

     忽略 /favicon.ico

     直接返回 HTML 文件内容

     否则走代理 ApiGateway


     * options method spt
     * favicon.ioc
     * html ,json file spt
     *
     * @return ApiGateway.response
     * @param exchange
     * @throws IOException
     */
    private static void handleAllReq(@NotNull HttpExchange exchange) throws Exception {


        @NotNull String path1 = getPathNoQuerystring(exchange);
        System.out.println("fun handleAllReq(path="+path1+")");
            @NotNull HttpHandler proxyObj = new ApiGateway(path1);
            proxyObj.handle(exchange);


    }



    public  static     String webroot=getPrjPath()+"/webroot";
    /**
     *
     //listAdm
     String tmpleFileName = path1.substring(0,path1.length()-4);

     if(path1.endsWith(".html"))
     tmpleFileName = path1.substring(0,path1.length()-5);
     */

    /**
     * 获取path ，不要带Querystring
     *
     * @param exchange
     * @return
     */
    public static String getPathNoQuerystring(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        return path;
    }

//    server.createContext("/UserCentrHdr", new UserCentrHdr());
    // container.getComponent(RegHandler.class)
    //     server.createContext("/reg",getBeanFrmSpr(RegHandler.class));
    //     server.createContext("/login", getBeanFrmSpr(LoginHdr.class));
//        server.createContext("/QueryUsr",getBeanFrmSpr(QueryUsrHdr.class) );
//        server.createContext("/BetHdr",getBeanFrmSpr(BetHdr.class));
//                //IocSpringCfg.   context. getBean(BetHdr.class));
//        server.createContext("/QryOrdBetHdr", new QryOrdBetHdr());
//        server.createContext("/QryTeamHdr", new QryTeamHdr());


//        AddOrdBetHdr bean = context.getBean(AddOrdBetHdr.class);


    //    server.createContext("/rechargeHdr",  getBeanFrmSpr(RechargeHdr.class) );
//        server.createContext("/rechargeHdr",
//                getBeanFrmSpr(RechargeHdr.class)
//        );
//        server.createContext("/QueryOrdChrgHdr", new QueryOrdChrgHdr());
//
    // 读取类的path注解
    // 读取类的路径注解
    public static String getPathFromBean(Class<?> aClass) {


        if (aClass.isAnnotationPresent(Path.class)) {
            Path mapping = aClass.getAnnotation(Path.class);
            return mapping.value();  // 可能有多个路径
        }

        // 查找 @RequestMapping（类级别）
//        if (aClass.isAnnotationPresent(RequestMapping.class)) {
//            RequestMapping mapping = aClass.getAnnotation(RequestMapping.class);
//            return String.join(", ", mapping.value());  // 可能有多个路径
//        }
        // 兼容 @GetMapping、@PostMapping 等（可选）
        return getPathFromAnnotations(aClass);
    }

    public static String[] getPathsFromBean(Class<?> aClass) {


        if (aClass.isAnnotationPresent(Paths.class)) {
            Paths mapping = aClass.getAnnotation(Paths.class);
            assert mapping != null;
            return mapping.value();  // 可能有多个路径
        }

        return new String[]{};


    }

    // 处理其他 Spring Mapping 注解
    private static String getPathFromAnnotations(AnnotatedElement element) {
        for (Annotation annotation : element.getAnnotations()) {
//            if (annotation instanceof GetMapping) {
//                return String.join(", ", ((GetMapping) annotation).value());
//            }
//            if (annotation instanceof PostMapping) {
//                return String.join(", ", ((PostMapping) annotation).value());
//            }
//            if (annotation instanceof PutMapping) {
//                return String.join(", ", ((PutMapping) annotation).value());
//            }
//            if (annotation instanceof DeleteMapping) {
//                return String.join(", ", ((DeleteMapping) annotation).value());
//            }
        }
        return "/defPathhhhhh";
    }





    //        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
//        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials","true");

}


