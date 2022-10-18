package me.RonanCraft.Pueblos.customevents;

import me.RonanCraft.Pueblos.claims.data.members.Member;
import org.bukkit.event.Cancellable;

//Called when a member leaves a claim
public class PueblosEvent_MemberLeave extends PueblosEventType_ClaimCancellable implements Cancellable {

    private final Member member;

    public PueblosEvent_MemberLeave(Member member) {
        super(member.claimData, true);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
