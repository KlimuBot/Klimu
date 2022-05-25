package eus.klimu.channel.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.channel.domain.model.Channel;
import eus.klimu.channel.domain.service.definition.ChannelService;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.security.TokenManagement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImp implements ChannelService {

    @Getter
    private enum ChannelURL {

        BASE("/"), NAME("/name/"), GET_ALL("/all"), CREATE("/create"),
        CREATE_ALL("/create/all"), UPDATE("/update"), DELETE("/delete");

        private final String name;

        ChannelURL(String name) {
            this.name = "https://klimu.eus/RestAPI/channel" + name;
        }
    }

    private final HttpSession session;
    private final Gson gson = new Gson();
    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public Channel getChannel(long id) {
        ResponseEntity<String> response = requestMaker.doGet(
                ChannelURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Channel.class);
        }
        return null;
    }

    @Override
    public Channel getChannel(String name) {
        ResponseEntity<String> response = requestMaker.doGet(
                ChannelURL.NAME.getName() + name,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Channel.class);
        }
        return null;
    }

    @Override
    public List<Channel> getAllChannels() {
        ResponseEntity<String> response = requestMaker.doGet(
                ChannelURL.GET_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        return channelsToList(response);
    }

    @Override
    public Channel addNewChannel(Channel channel) {
        ResponseEntity<String> response = requestMaker.doPost(
                ChannelURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(channel, Channel.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Channel.class);
        }
        return null;
    }

    @Override
    public List<Channel> addAllChannels(List<Channel> channels) {
        ResponseEntity<String> response = requestMaker.doPost(
                ChannelURL.CREATE_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(channels)
        );
        return channelsToList(response);
    }

    @Override
    public Channel updateChannel(Channel channel) {
        ResponseEntity<String> response = requestMaker.doPut(
                ChannelURL.UPDATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(channel, Channel.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Channel.class);
        }
        return null;
    }

    @Override
    public void deleteChannel(Channel channel) {
        ResponseEntity<String> response = requestMaker.doDelete(
                ChannelURL.DELETE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(channel, Channel.class)
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    private List<Channel> channelsToList(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            JSONArray jsonChannels = new JSONArray(response.getBody());
            List<Channel> channelResponse = new ArrayList<>();

            jsonChannels.forEach(channel -> channelResponse.add(gson.fromJson(channel.toString(), Channel.class)));
            return channelResponse;
        }
        return Collections.emptyList();
    }
}
