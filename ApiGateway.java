package util.serverless;

import cfg.MinValidator;
import entityx.usr.NonDto;
import handler.NoWarpApiRsps;
import jakarta.ws.rs.Produces;
import model.other.ContentType;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.jetbrains.annotations.Nullable;
import orgx.uti.context.ThreadContext;
import orgx.uti.http.RegMap;
import util.annos.*;
import util.ex.ValideTokenFailEx;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.excptn.ExceptionObj;
import jakarta.annotation.security.PermitAll;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.SecurityContext;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import util.excptn.ExceptionBaseRtm;
import util.algo.Icall;
import util.auth.IsEmptyEx;
import util.model.Context;
import util.oo.HttpUti;
import util.rest.RestUti;

import java.io.IOException;
import java.lang.reflect.*;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import java.util.Map;

// static cfg.BaseHdr.*;


//import static cfg.Containr.sessionFactory;
import static cfg.Containr.*;
import static cfg.WebSvr.getPathNoQuerystring;
import static cfg.WebSvr.pathClzMap;
import static orgx.uti.Uti.getMediaType;
import static orgx.uti.http.RegMap.registerMapping;
import static util.algo.GetUti.*;
import static util.algo.ToXX.toDtoFrmHttp;
import static util.auth.AuthUtil.request_getHeaders_get;
import static util.excptn.ExptUtil.*;
import static util.misc.RecordMapper.mapToRecord;
import static util.misc.RestUti.pathMthMap;
import static util.misc.RflktUti.*;
import static util.oo.HttpUti.getParamMap;
import static util.proxy.AopUtil.ivk4log;
import static util.auth.AuthUtil.getCurrentUser;
import static util.log.ColorLogger.*;


import static util.serverless.ApiGatewayResponse.createErrResponseWzErrcode;
import static util.misc.Util2025.*;
import static util.tx.TransactMng.*;
import static util.tx.dbutil.setField;
import static util.misc.util2026.*;

/**
 * API Gateway...api proxy
 * 多个拦截器（责任链模式）
 * 如果需要多个拦截器，可以链式包装： 增强复用性
 * <p>
 * server.createContext("/test",
 * new AuthInterceptor(new LoggingInterceptor(new MyHandler())));
 */
//aop shuld log auth ,ex catch,,,pfm
public class ApiGateway implements HttpHandler {
    public static final String ChkLgnStatSam = "ChkLgnStatSam";
    public String path;
    private Object target; // 目标对象 for compt,,frm icall to obj type

    static ThreadLocal<Object> targetThreadLocal = new ThreadLocal<>();

    public @NotNull ApiGateway(String path1) throws Exception {

        this.path = path1;
        Object hdlr;
        @NotNull Class<?> hdrclas = pathClzMap.get(path1);
        if (hdrclas != null)
            hdlr = hdrclas.getConstructor().newInstance();
        else {
            hdlr = pathMthMap.get(path1);

        }


        if (hdlr == null)
            throw new CantFindPathEx("path=" + path1);


        this.target = hdlr;
        targetThreadLocal.set(hdlr);

    }

//    public static void main(String[] args) throws Exception {
//        Object obj1 = RechargeHdr.class.getConstructor().newInstance();
//        setField(obj1, SessionFactory.class,  AppConfig. sessionFactory);
//        //new RechargeHdr(); // 目标对象
//        Object proxyObj = new AtProxy4webapi(obj1); // 创建
//        HttpHandler hx= (HttpHandler) proxyObj;
//        hx.handle(null);
//    }


    /**
     * ivk  here,,,aop log,maybe need decra mode,,not pxy mod
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public @NotNull Object invoke_callNlogWarp(@NotNull Object dto) throws Throwable {

        //funName jst 4 lg
        String mthFullname = target.getClass().getName() + ".call/hdlrRq";


        //---------blk chk auth
        Object result = ivk4log(mthFullname, dto, () -> {
            // injectAll4spr(target);
            if (isImpltInterface(target, RequestHandler.class))
                return ((RequestHandler) target).handleRequest(dto, null);
            else if (isImpltInterface(target, Icall.class)) {
                return ((Icall) target).main(dto);
            } else if (target instanceof Method) {
                Method m = (Method) target;
                Object retobj;
                if (dto instanceof NullDto)
                    retobj = m.invoke(getObjByMthd(m));
                else
                    retobj = m.invoke(getObjByMthd(m), dto);

                if (m.isAnnotationPresent(Produces.class)) {
                    Produces anno = m.getAnnotation(Produces.class);
                    String[] value = anno.value();
                    if (value[0].equals("image/png"))
                        throw new BreakEx("");
                }

                if (m.isAnnotationPresent(NoWarpApiRsps.class))
                    return retobj;
                else {
                    var apigtwy = new ApiGatewayResponse(retobj);
                    return apigtwy;
                }

            } else {
                Method m = getMethodByObj(target, "handleRequest");
                var retobj = m.invoke(target, dto);

                var apigtwy = new ApiGatewayResponse(retobj);
                return apigtwy;
            }


        });


        return result;
    }

    private Object getObjByMthd(Method m) {
        if (Modifier.isStatic(m.getModifiers())) {
            return null; // 静态方法不需要实例
        }
        try {
            return m.getDeclaringClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance for method: " + m.getName(), e);
        }
    }


    //    // 生成代理对象
//    public static Object createProxy4webapi(Object target) {
//       // Class<?>[] interfaces = target.getClass().getInterfaces();
//     //   System.out.println("crtProxy().itfss="+encodeJsonObj(interfaces));
//        return new AtProxy4webapi(target);
//    }
    public static ThreadLocal<HttpExchange> httpExchangeCurThrd = new ThreadLocal<>();

    /**
     * Handle the given request and generate an appropriate response.
     * See {@link HttpExchange} for a description of the steps
     * involved in handling an exchange.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is {@code null}
     * @throws IOException          if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        setContextThrd(exchange);

        String mth = colorStr("handle", YELLOW_bright);
        String prmurl = colorStr(String.valueOf(exchange.getRequestURI()), GREEN);
        System.out.println("▶\uFE0Ffun " + mth + "(url=" + prmurl);
        //   curUrlPrm.set(exchange.getrequ);
        var responseTxt = "";
        // ExceptionObj ex = new ExceptionObj("");
        try {
            //   setcookie("uname", "007", exchange);//for test

            //============aop trans begn
            beginx();

            //---------blk chk auth
            urlAuthChkV2(exchange);

            //injkt ctx
            Context ctx = new Context();
            ctx.sessionFactory = sessionFactory;
            ctx.session = sessionFactory.getCurrentSession();
            ctx.cfg = cfgMap;
            if (needLoginUserAuth(this.target)) {
                ctx.currUsername = getCurrentUser();
                ThreadContext.remoteUser.set(ctx.currUsername);
                ;

            }

            RestUti.contextThdloc.set(ctx);
            // hdl
            handlexProcess(exchange);

            commitx();
            //      System.out.println("endfun handle2()");
            System.out.println("✅endfun handle()");
            return;

        } catch (Throwable e8) {
            // transactionThreadLocal.get().rollback();
            rollbackTx();
            printStackTrace(e8);
            //---------warp ex obj
            Throwable e = getRawEx(e8);
            ExceptionObj ex = getExceptionObjFrmE(e);
            System.out.println(ex);
            addInfo2ex(ex, e);
            //-----------print exobj
            ApiGatewayResponse errResponseWzErrcode = createErrResponseWzErrcode(ex);
            responseTxt = encodeJson4ex(errResponseWzErrcode);
            wrtRespErr(exchange, responseTxt);


        } finally {
            sessionFactory.getCurrentSession().close();// 关闭 session，但不会提交事务
            ThreadLocalSessionContext.unbind(sessionFactory);
            //  exchange.close();
        }
        //end catch


        //not ex ,just all ok blk
        //ex.fun  from stacktrace
        System.out.println("\uD83D\uDED1 endfun handle().ret=" + responseTxt);
    }

    private void setContextThrd(HttpExchange httpExchange) {
        lastExsList.set(new ArrayList<>());
        httpExchangeCurThrd.set(httpExchange);
        HttpUti.httpExchangeCurThrd.set(httpExchange);
        curCtrlCls.set(this.target.getClass());
        ThreadContext.currHttpExchange.set(httpExchange);
        ;
        curUrl.set(encodeJson(httpExchange.getRequestURI()));
        requestIdCur.set(getUUid());
    }

    @org.jetbrains.annotations.NotNull
    private static ExceptionObj getExceptionObjFrmE(Throwable e) {
        ExceptionObj ex;
        if (e instanceof ExceptionObj) {
            //my throw ex.incld funprm
            ex = (ExceptionObj) e;
            ex.errcode = e.getClass().getName();


        } else if (e instanceof ExceptionBaseRtm) {
            ex = new ExceptionObj(e.getMessage());

            //cvt to cstm ex
            String message = e.getMessage();
            ex = new ExceptionObj(message);
            ex.cause = e;
            ex.errcode = ((ExceptionBaseRtm) e).getType();
        } else {
            //nml err
            ex = new ExceptionObj(e.getMessage());

            //cvt to cstm ex
            String message = e.getMessage();
            ex = new ExceptionObj(message);
            ex.cause = e;
            ex.errcode = e.getClass().getName();

        }
        return ex;
    }

    @Nullable
    private static Throwable getRawEx(Throwable e) {
        Throwable ee = e;  //deft is nml ex
        if (e instanceof InvocationTargetException) {
            ee = e.getCause();
        }
        return ee;
    }

    private static void printStackTrace(Throwable e) {
        printLn("---------------------print1134 ex ()");
        e.printStackTrace();
        System.out.flush();  // 立即刷新缓冲区
        System.err.flush();  // 立即刷新缓冲区
        sleepx(20);
        printLn("---------------------end print ex ()");
    }

//    @Inject
//    @Qualifier(ChkLgnStatSam)  //"ChkLgnStatSam"
//
    //   public ISAM sam1;

//    @Inject
//    
//    @Qualifier("ChkLgnStatAuthenticationMechanism")
//    public HttpAuthenticationMechanism HttpAuthenticationMechanism1;

    //public  static
    private void urlAuthChkV2(HttpExchange exchange) throws ValideTokenFailEx, AuthenticationException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        injectAll4spr(this);

        if (needLoginUserAuth(this.target)) {
            //apigat reqauth mode...
            var aClass = this.target.getClass();
            if (aClass.isAnnotationPresent(RequireAuth.class)) {
                //THIS OLY FOR admin page auth chk,,cookie mode
                RequireAuth RequireAuth1 = aClass.getAnnotation(RequireAuth.class);
                var authfun1 = RequireAuth1.authFun();
                var obj = authfun1.getConstructor().newInstance();
                obj.validateRequest(null, null, null);
            } else {
                //chk token blk list  ,,jwt mode ,dft chk mode

                sam4chkLgnStat.validateRequest(null, null, null);
                var uid = request_getHeaders_get("X-MS-CLIENT-PRINCIPAL-NAME");

            }

        }
    }

    private void injectAll4spr(ApiGateway apiGateway) {

    }


//            if (authStt == AuthenticationStatus.SUCCESS) {
//                //next prcs
//            } else {
//                ValideTokenFailEx 需要登录 = new ValideTokenFailEx("登录标识校验失败");
//                需要登录.lastExsList=ExptUtil.lastExsList.get();
//                throw  需要登录;
//            }

    protected static boolean needLoginUserAuth(Method target) {


        Method mth = (Method) target;
        return !mth.isAnnotationPresent(PermitAll.class);


    }

    protected static boolean needLoginUserAuth(Object target) {

        //here target also canbe a method
//        Class<?> aClass = this.getClass();
//        if (aClass == ApiGateway.class) {
//            aClass = this.target.getClass();
//        }


        if (target instanceof Method) {
            Method mth = (Method) target;
            return !mth.isAnnotationPresent(PermitAll.class);

        }


        //here also can be a mthd
        if (target instanceof Class<?>) {
            Class<?> aClass = (Class<?>) target;
            boolean annotationPresent = aClass.isAnnotationPresent(PermitAll.class);
            //if has anno ,not need login
            return !annotationPresent;
        }

        return false;
    }

    SecurityContext SecurityContext1;

    private void handlexProcess(HttpExchange httpExchange) throws Throwable {
        String prmurl;
        String mth;
        //basehdr.kt
        //-----------------stat trans action
        //  System.out.println("▶\uFE0Ffun handle2(HttpExchange)");

        String path = getPathNoQuerystring(httpExchange);
        Method method = pathMthMap.get(path);
        if (method != null) {
            RegMap.registerMapping(path, method, httpExchange);
            return;
        }

        Class<?> cls = pathClzMap.get(path);
        if (cls != null) {
            if (isImpltItfs(cls, RequestHandler.class)) {
                Method m = getMethod4faasFun(cls, "handleRequest");
                m.setAccessible(true);
                //  RegMap.registerMapping(path, m, httpExchange);
                Object dto=RegMap.getDto( m,  httpExchange);
                Object o=cls.getConstructor().newInstance();
                //if (isImpltInterface(target, RequestHandler.class))
                RequestHandler o1 = (RequestHandler) o;
                  Object rzt= o1.handleRequest(dto, null);
                String mediaType = getMediaType(m, Produces.class);
               // RegMap.registerMapping(path, m, httpExchange);
               RegMap.write(httpExchange,mediaType,rzt);
               return;
            }
            //
            else if (isImpltItfs(cls, Icall.class)) {
                Method m = getMethod4faasFun(cls, "main");

                m.setAccessible(true);
                RegMap.registerMapping(path, m, httpExchange);
                return;
                // return ((Icall) target).main(dto);
            } else {
                Method m = getMethod4faasFun(cls, "handleRequest");
                if (m != null) {
                    m.setAccessible(true);
                    RegMap.registerMapping(path, m, httpExchange);
                    return;
                }

            }
        }

        Object rzt;
        //---------log
        Object dto = getDto(httpExchange);
        try {
            rzt = invoke_callNlogWarp(dto);
        } catch (BreakEx e) {
            return;
        }


        //  默认返回 JSON，不需要额外加 @ResponseBody
        //  默认会将 String 直接作为 text/plain 处理：
        if (rzt == null)
            rzt = "ok";
        //  rzt=new ApiResponse(rzt);
        if (rzt.getClass() == String.class)
            wrtResp(httpExchange, rzt.toString(), ContentType.TEXT_HTML.getValue());
        else
            wrtResp(httpExchange, encodeJsonByJackjson(rzt));

        /**
         * ✅ 方法三：使用 Jackson 替代 Gson（兼容更好）
         * Jackson 处理异常对象的序列化更宽容，通常不会抛出这种访问异常：
         *
         * java
         * Copy
         * Edit
         * ObjectMapper mapper = new ObjectMapper();
         * String json = mapper.writeValueAsString(exceptionObject);
         */


        /// ----------log


    }

    @org.jetbrains.annotations.NotNull
    @Deprecated
    private Object getDto(HttpExchange exchange) throws Exception {


        try {
            Class prmDtoCls = getDtoCls();
            Object dto;

            dto = getDtoClzRcdTypeNwzCurrUfld(exchange, prmDtoCls);


            // addDeftParam(dto);
            validDtoByMe(dto);
            return dto;
        } catch (CantFindPrmEx e) {
            return new NullDto();
        }

    }

    @org.jetbrains.annotations.NotNull
    public static Object getDtoClzRcdTypeNwzCurrUfld(HttpExchange exchange, Class prmDtoCls) throws Exception {
        Object dto;
        if (NonDto.class.isAssignableFrom(prmDtoCls)) {  // 判断类型是否是 NonDto 或其子类
            dto = new NonDto();
        } else if (prmDtoCls.isRecord()) {
            dto = mapToRecord(prmDtoCls, getParamMap(exchange));
        } else {
            dto = toDtoWzCurrUfld(exchange, prmDtoCls);  // 假设 toDto 返回 Object 或 dto 类型
        }
        return dto;
    }

    @NotNull
    private Class getDtoCls() throws CantFindPrmEx, CantFindPrmDtoEx {
        Class PrmDtoCls = null;

        if (isMth(this.target)) {
            //   try{
            PrmDtoCls = getPrmClassFrmMth((Method) this.target);
//            } catch (CantFindPrmEx e) {
//                System.out.println(e.printStackTrace(););
//            }
        } else if (hasMtd(this.target, "handleRequest")) {
            PrmDtoCls = getFirstPrmClassFrmMthd(this.target, "handleRequest");
            // getPrmClass(this.target, "handleRequest");
        } else if (hasMtd(this.target, "main")) {
            PrmDtoCls = getFirstPrmClassFrmMthd(this.target, "main");
        } else if (isNotMth(this.target)) {
            PrmDtoCls = getFirstPrmClassFrmMthd(this.target, "main");
        }
        if (PrmDtoCls == null)
            throw new CantFindPrmDtoEx("target=" + this.target.toString());
        return PrmDtoCls;
    }

    private @NotNull Class getPrmClassFrmMth(@NotNull Method method) throws CantFindPrmEx {

        // 确保方法名称匹配
//        if (!method.getName().equals(methodName)) {
//            continue;
//        }

        // 遍历方法的所有参数
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0)
            throw new CantFindPrmEx("m=" + method.getName());
        Parameter parameter = parameters[0];
        Class<?> type = parameter.getType();
        if (type == Object.class)
            throw new CantFindPrmEx("m=" + method.getName());
        return type; // 返回参数的 Class 类型
    }

    private boolean isMth(Object target) {
        if (target instanceof Method) {
            return true;
        }
        return false;
    }

    private boolean isNotMth(Object target) {
        return !isMth(target);
    }


    /**
     * 是否包含方法
     *
     * @param target     target一般是个object，也可能是个method
     * @param methodName
     * @return
     */
    private boolean hasMtd(Object target, String methodName) {
        if (target == null || methodName == null) {
            return false;
        }

        // 如果传入的是一个 Method 对象
        if (target instanceof Method) {
            return false;
        }

        // 否则，认为是 Object，遍历其所有方法
        Class<?> clazz = target.getClass();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }

        return false;
    }

    // @Nullable
    @NotNull
    private static @NotNull Object toDtoWzCurrUfld(HttpExchange exchange, @NotNull Class Dtocls) throws Exception, IsEmptyEx {

        // 反射创建 DTO 实例
        Object dto = Dtocls.getDeclaredConstructor().newInstance();


        var dtoFrmHttp = toDtoFrmHttp(exchange, Dtocls);
        // copyProps(dtoFrmHttp, dto);
        dto = dtoFrmHttp;

        //--------set cook to dto
//        List<CookieParam> cookieParams = getCookieParamsV2(target.getClass(), "call");
//        for (CookieParam cknm : cookieParams) {
//            String v = getcookie(cknm.name(), httpExchangeCurThrd.get());
//            if (cknm.value().equals("$curuser"))
//                v = getCurrentUser();
//            setField(dto, cknm.name(), v);
//        }
        //------set jwt to dto
//        List<JwtParam> JwtParams = getParams(target.getClass(), JwtParam.class);
//        for (JwtParam jw : JwtParams) {
//            if (jw.name().equals("uname"))
//                setField(dto, jw.name(), getCurrentUser());
//        }


        //SET getUsernameFrmJwtToken(httpExchangeCurThrd.get()
        injectCurrUserFld(dto);
        return dto;
    }


    private static void injectCurrUserFld(Object dto) throws IllegalAccessException {
        Field[] flds = dto.getClass().getFields();
        for (Field fld : flds) {
            if (fld.isAnnotationPresent(CurrentUsername.class))
                if (needLoginUserAuth(targetThreadLocal.get())) {
                    fld.set(dto, getCurrentUser());
                }
            //  setField(dto, jw.name(), getCurrentUser());
        }
    }

    //uname cookie
    private void addDeftParam(Object dto) {

        if (needLoginUserAuth(this.target))
            setField(dto, "uname", getCurrentUser());
        else
            setField(dto, "uname", "defusr");
    }

    public static void validDtoByMe(Object dto) {
        System.out.println("fun validdto(cls=" + dto.getClass() + ")");
        var clazz = dto.getClass();
        // 遍历类的所有字段
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("字段: " + field.getName());

            // 遍历该字段上的所有注解
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                System.out.println("  注解: " + annotation.annotationType().getSimpleName());
                if (annotation.annotationType() == Min.class) {
                    MinValidator vldr = new MinValidator();
                    Min annotation1 = (Min) annotation;
                    vldr.initialize(annotation1);
                    if (!vldr.isValid((BigDecimal) getField(dto, field.getName()), null)) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("handler/dto", dto);
                        m.put("fld", field.getName());
                        m.put("msg", "vldfail");
                        m.put("msgAnno", annotation1.message());
                        throw new RuntimeException(encodeJsonObj(m));
                    }
                    ;
                }

                if (annotation.annotationType() == NotBlank.class) {
                    NotBlankValidator vldr = new NotBlankValidator();
                    NotBlank annotation1 = (NotBlank) annotation;

                    String field1 = (String) getField(dto, field.getName());
                    if (!vldr.isValid(
                            field1, null
                    )) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("handler/dto", dto);
                        m.put("fld", field.getName());
                        m.put("msg", "vldfail");
                        m.put("msgAnno", annotation1.message());
                        throw new RuntimeException(encodeJsonObj(m));
                    }
                    ;
                }

                if (annotation.annotationType() == NotNull.class) {
                    NotNullValidator vldr = new NotNullValidator();
                    NotNull annotation1 = (NotNull) annotation;

                    if (!vldr.isValid(getField(dto, field.getName()), null)) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("handler/dto", dto);
                        m.put("fld", field.getName());
                        m.put("msg", "vldfail");
                        m.put("msgAnno", annotation1.message());
                        throw new RuntimeException(encodeJsonObj(m));
                    }
                    ;
                }
            }
        }

    }


    public static String processInvkExpt(HttpExchange exchange, InvocationTargetException e) throws IOException {
        ExceptionObj ex;
        ex = new ExceptionObj(e.getMessage());
        ex.cause = e;
        Throwable cause = e.getCause();

        ex.errcode = cause.getClass().getName();
        ex.errmsg = cause.getMessage();


        addInfo2ex(ex, e);

        String responseTxt = encodeJson4ex(createErrResponseWzErrcode(ex));
        //   String responseTxt = encodeJson(ex);

        wrtRespErr(exchange, responseTxt);

        return responseTxt;
    }

    //protected abstract void urlAuthChk(HttpExchange exchange) throws Exception;


    //----------aop auth
//    private void urlAuthChk(HttpExchange exchange) throws IOException, NeedLoginEx {
//        if (AuthService.needLoginAuth(exchange.getRequestURI())) {
//            String uname = getcookie("uname", exchange);
//            //  uname="ttt";
//            if (uname.equals("")) {
//                //need login
//                NeedLoginEx e = new NeedLoginEx("需要登录");
//
//                e.fun = "BaseHdr." + getCurrentMethodName();
//                e.funPrm = toExchgDt((HttpExchange) exchange);
//
//             //   addInfo2ex(e, null);
//
//                throw  e;
//            }
//
//            //basehdr.kt
//            //-----------------stat trans action
//            //  System.out.println("▶\uFE0Ffun handle2(HttpExchange)");
//
//        }
//
//
//    }

}
