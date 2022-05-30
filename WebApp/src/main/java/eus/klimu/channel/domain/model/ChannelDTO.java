package eus.klimu.channel.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO implements Serializable {

    private Long id;
    private String name;
    private String icon;

    public static ChannelDTO fromChannel(Channel channel) {
        ChannelDTO channelDTO = new ChannelDTO();

        channelDTO.setId(channel.getId());
        channelDTO.setName(channel.getName());
        channelDTO.setIcon(channel.getIcon());

        return channelDTO;
    }

}
