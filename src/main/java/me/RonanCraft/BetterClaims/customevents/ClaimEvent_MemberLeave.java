package me.RonanCraft.BetterClaims.customevents;

import me.RonanCraft.BetterClaims.claims.data.members.Member;
import org.bukkit.event.Cancellable;

//Called when a member leaves a claim
public class ClaimEvent_MemberLeave extends ClaimEventType_ClaimCancellable implements Cancellable {

    private final Member member;

    public ClaimEvent_MemberLeave(Member member) {
        super(member.claimData, false);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
