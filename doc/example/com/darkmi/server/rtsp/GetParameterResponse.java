package com.darkmi.server.rtsp;

import com.darkmi.server.core.RtspController;
import com.darkmi.server.session.RtspSession;
import com.darkmi.server.util.DateUtil;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class GetParameterResponse implements Callable<HttpResponse> {
    private static Logger logger = LoggerFactory.getLogger(GetParameterResponse.class);
    private HttpRequest request = null;

    public GetParameterResponse(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse call() throws Exception {
        HttpResponse response = null;
        // get cesq
        String cseq = request.headers().get(RtspHeaderNames.CSEQ).toString();
        if (null == cseq || "".equals(cseq)) {
            logger.error("cesq is null.........");
            response =
                    new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
                            RtspResponseStatuses.INTERNAL_SERVER_ERROR);
            response.headers().set(RtspHeaderNames.SERVER, RtspController.SERVER);
            response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
            return response;
        }

        // get require
        String require = request.headers().get(RtspHeaderNames.REQUIRE).toString();
        if (null == require || "".equals(require)
                || (!require.equals(RtspController.REQUIRE_VALUE_NGOD_R2))) {
            logger.error("require is {}.........", require);
            response =
                    new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
                            RtspResponseStatuses.INTERNAL_SERVER_ERROR);
            response.headers().set(HttpHeaderNames.SERVER, RtspController.SERVER);
            response.headers().set(RtspHeaderNames.CSEQ, request.headers().get(RtspHeaderNames.CSEQ));
            response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
            return response;
        }

        String sessionKey = this.request.headers().get(RtspHeaderNames.SESSION).toString();
        if (null == sessionKey || "".equals(sessionKey)) {
            response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.BAD_REQUEST);
            response.headers().set(RtspHeaderNames.SERVER, RtspController.SERVER);
            response.headers().set(RtspHeaderNames.CSEQ, request.headers().get(RtspHeaderNames.CSEQ));
            response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
            return response;
        }

        // get session
        RtspSession rtspSession = RtspController.sessionAccessor.getSession(sessionKey, false);
        if (null == rtspSession) {
            logger.error("rtspSession is null.");
            response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.BAD_REQUEST);
            response.headers().set(RtspHeaderNames.SERVER, RtspController.SERVER);
            response.headers().set(RtspHeaderNames.CSEQ, request.headers().get(RtspHeaderNames.CSEQ));
            response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
            return response;
        }

        response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
        response.headers().set(RtspHeaderNames.CSEQ, request.headers().get(RtspHeaderNames.CSEQ));
        response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
        response.headers().set(RtspHeaderNames.DATE, DateUtil.getGmtDate());
        response.headers().set(RtspHeaderNames.SESSION, sessionKey);
        response.headers().set(RtspHeaderNames.RANGE, request.headers().get(RtspHeaderNames.RANGE));

        String scale = request.headers().get(RtspHeaderNames.SCALE).toString();
        if (null != scale) {
            response.headers().set(RtspHeaderNames.SCALE, scale);
        } else {
            response.headers().set(RtspHeaderNames.SCALE, "1.00");
        }
        return response;
    }

}