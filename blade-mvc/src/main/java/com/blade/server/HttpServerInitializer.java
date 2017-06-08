package com.blade.server;

import com.blade.Blade;
import com.blade.Environment;
import com.blade.metric.Connection;
import com.blade.metric.WebStatistics;
import com.blade.mvc.Const;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.time.LocalDateTime;

/**
 * HttpServerInitializer
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	private final Blade blade;
	private final Environment environment;
	private final SslContext sslCtx;

	public HttpServerInitializer(Blade blade, SslContext sslCtx) {
		this.blade = blade;
		this.environment = blade.environment();
		this.sslCtx = null;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		ChannelPipeline p = ch.pipeline();

		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}

		Connection ci = null;
		if (environment.getBoolean(Const.ENV_KEY_MONITOR_ENABLE, true)) {
			ci = new Connection();
			ci.setIp(WebStatistics.getIpFromChannel(ch));
			ci.setEstablished(LocalDateTime.now());
			WebStatistics.me().addConnectionInfo(ci);
			p.addLast(new ChannelTrafficCounter(0, ci));
		}

		if (environment.getBoolean(Const.ENV_KEY_GZIP_ENABLE, false)) {
			p.addLast(new HttpContentCompressor());
		}

		p.addLast(new HttpServerCodec());
		p.addLast(new HttpServerExpectContinueHandler());
		p.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
		p.addLast(new ChunkedWriteHandler());
		p.addLast(new HttpServerHandler(blade, ci));
	}
}
