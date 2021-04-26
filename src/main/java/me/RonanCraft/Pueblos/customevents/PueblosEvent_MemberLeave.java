package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PueblosEvent_MemberLeave extends ClaimEvent implements Cancellable {

    private final ClaimMember member;

    public PueblosEvent_MemberLeave(ClaimMember member) {
        super(member.claim);
        this.member = member;
    }

    public ClaimMember getMember() {
        return member;
    }
}
