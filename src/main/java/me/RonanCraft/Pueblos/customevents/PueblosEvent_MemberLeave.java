package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import org.bukkit.event.Cancellable;

//Called when a member leaves a claim
public class PueblosEvent_MemberLeave extends PueblosEventType_ClaimCancellable implements Cancellable {

    private final ClaimMember member;

    public PueblosEvent_MemberLeave(ClaimMember member) {
        super(member.claim);
        this.member = member;
    }

    public ClaimMember getMember() {
        return member;
    }
}
