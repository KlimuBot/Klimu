package eus.klimu.channel.domain.service.definition;

import eus.klimu.channel.domain.model.Channel;

import java.util.List;

public interface ChannelService {

    Channel getChannel(long id);
    Channel getChannel(String name);
    List<Channel> getAllChannels();
    Channel addNewChannel(Channel channel);
    List<Channel> addAllChannels(List<Channel> channels);
    Channel updateChannel(Channel channel);
    void deleteChannel(Channel channel);

}
