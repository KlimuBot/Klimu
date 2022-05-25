package eus.klimu.channel.api;

import eus.klimu.channel.domain.service.definition.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping("/subscription")
    public String getSubscriptionPage(Model model) {
        log.info("Fetching the subscription page");
        model.addAttribute("channelList", channelService.getAllChannels());

        return "services/suscripciones";
    }

    @GetMapping("/{channel}")
    public String getChannelPage(@PathVariable String channel, Model model) {
        return "services/listaAlertas";
    }
}
