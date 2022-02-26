package net.rocketpowered.connector.client.gui.guild;

import com.craftingdead.immerse.client.gui.screen.Theme;
import com.craftingdead.immerse.client.gui.view.AvatarView;
import com.craftingdead.immerse.client.gui.view.Color;
import com.craftingdead.immerse.client.gui.view.ParentView;
import com.craftingdead.immerse.client.gui.view.TextView;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.rocketpowered.api.Rocket;
import net.rocketpowered.api.gateway.GameClientGateway;
import net.rocketpowered.common.GuildRank;
import net.rocketpowered.common.payload.GuildMemberLeaveEvent;
import net.rocketpowered.common.payload.GuildMemberPayload;
import net.rocketpowered.common.payload.GuildPayload;
import net.rocketpowered.common.payload.UserPresencePayload;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

public class MemberView extends ParentView {

  private GuildPayload guild;

  private GuildMemberPayload member;

  private Disposable removeListener;
  private Disposable presenceListener;

  private final AvatarView avatarView;
  private final TextView nameView;
  private final TextView rankView;

  public MemberView(GuildPayload guild, GuildMemberPayload member) {
    super(new Properties<>().styleClasses("item").unscaleBorder(false).focusable(true));

    this.guild = guild;

    this.avatarView = new AvatarView(new Properties<>(),
        new GameProfile(member.getUser().getMinecraftProfile().getId(), null));
    this.avatarView.defineBorderColorState(Theme.OFFLINE);
    this.addChild(this.avatarView);

    var textView = new ParentView(new Properties<>().id("text"));
    this.addChild(textView);

    this.nameView = new TextView(new Properties<>())
        .setWrap(false)
        .setText(member.getUser().getMinecraftProfile().getName());
    this.nameView.getColorProperty().defineState(Theme.OFFLINE);
    textView.addChild(this.nameView);

    this.rankView = new TextView(new Properties<>()).setWrap(false);
    textView.addChild(this.rankView);

    this.updateMember(member);
  }

  public GuildMemberPayload getMember() {
    return this.member;
  }

  public void updateGuild(GuildPayload guild) {
    this.guild = guild;
    this.updateMember(this.member);
  }

  public void updateMember(GuildMemberPayload member) {
    this.member = member;

    var rank = member.getRank();
    var color = Color.WHITE;
    var owner = member.getUser().equals(this.guild.getOwner());
    if (owner) {
      color = Color.GOLD;
    } else if (rank == GuildRank.DIGNITARY) {
      color = Color.DARK_PURPLE;
    } else if (rank == GuildRank.ENVOY) {
      color = Color.AQUA;
    }

    this.rankView.setText(new TextComponent(owner ? "Owner" : rank.getDisplayName().orElse(""))
        .withStyle(ChatFormatting.ITALIC));
    this.rankView.getColorProperty().defineState(color);

    this.getLeftBorderColorProperty().defineState(color);
  }

  private void updatePresence(UserPresencePayload presence) {
    var color = presence.isOnline() ? Theme.ONLINE : Theme.OFFLINE;
    this.avatarView.defineBorderColorState(color);
    this.nameView.getColorProperty().defineState(color);
  }

  @Override
  protected void added() {
    super.added();
    this.removeListener = Rocket.getGameClientGatewayStream()
        .flatMap(GameClientGateway::getGuildEvents)
        .ofType(GuildMemberLeaveEvent.class)
        .filter(event -> event.getUser().equals(this.member.getUser()))
        .next()
        .subscribeOn(Schedulers.boundedElastic())
        .publishOn(Schedulers.fromExecutor(this.minecraft))
        .subscribe(__ -> {
          if (this.hasParent()) {
            var parent = this.getParent();
            parent.removeChild(this);
            parent.layout();
          }
        });

    this.presenceListener = Rocket.getGameClientGatewayStream()
        .flatMap(api -> api.getUserPresence(this.member.getUser().getId()))
        .subscribeOn(Schedulers.boundedElastic())
        .publishOn(Schedulers.fromExecutor(this.minecraft))
        .subscribe(this::updatePresence);
  }

  @Override
  protected void removed() {
    super.removed();
    this.removeListener.dispose();
    this.presenceListener.dispose();

  }
}
