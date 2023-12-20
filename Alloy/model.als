abstract sig Badge{
}
sig DefaultBadge extends Badge{
}
sig CreatedBadge extends Badge{
	creator: one User
}
sig User {
	badges: set Badge,
	friends: set User
}

sig Participant {
	user: one User
}

sig Team {
	participants: some Participant,
	battle: one Battle
}

sig Battle {
	tournament: one Tournament,
	creator: one User,
	teams: set Team,
}
sig Tournament {
	badges: set Badge,	
	participants: set Participant,
	OCT: one User,
	TCs: set User,
	battles: set Battle,
}{
	OCT not in TCs
}

// A OCT or TC can not be a Participant
fact CoordinatorNotParticipant{
	all t:Tournament, p: Participant |
	(p in t.participants) implies ((p.user != t.OCT) and (p.user not in t.TCs))
}

// Bidirectional relation Battle-Tournament
fact BattleLinkTournament {
	all t:Tournament, b: Battle | (b in t.battles) iff (b.tournament=t)
}

// Creator of a Battle must be OCT or TC
fact BattleCreator {
	all t:Tournament, b: Battle | (b in t.battles) implies (b.creator in (t.OCT+t.TCs))	
}

// Participants of Battle must be participants of Tournament
fact BattleParticipant {
	all t:Tournament, b:Battle | (b in t.battles) implies ( b.teams.participants in t.participants )
}

// Badge must be created by the OCT
fact BadgeCreation {
	all t: Tournament, b: Badge | (b in t.badges) implies ((b=CreatedBadge) iff (b.creator=t.OCT))
}

// If User has a Badge, then it is a participant of the Tournament with that Badge
fact AssignedBadge {
	all u: User, b: Badge| some t:Tournament | (b in u.badges) implies ((b in t.badges)and(u in t.participants.user))
}

// Bidirectional relation Team-Battle
fact BattleLinkTeam {
	all t:Team, b:Battle | (t in b.teams) iff (t.battle = b)
}

// non ci possono essere un partecipante in 2 team diversi della stessa battaglia
fact BattleTeam {
	all b:Battle| no disj t1,t2: Team | some p:Participant | 
	(t1 in b.teams and t2 in b.teams) and 
	(p in t1.participants and p in t2.participants)
}

// A User can participant to a Tournament only one time
fact PerticipantTournament {
	all t:Tournament| no disj p1,p2: Participant | some u:User | 
	(p1 in t.participants and p2 in t.participants) and 
	((u = p1.user) and (u = p2.user))
}

// Simmetric relation between friends
fact Friendlink {
	all u1,u2: User | (u2 in u1.friends) iff (u1 in u2.friends)
}

// Not reflective between friends
fact NotSelfFriends{
	all u: User | u not in u.friends
}

// Participant must exist in a Tournament
fact ParticipantInTournament{
	all p:Participant| one t:Tournament | (p in t.participants)
}

// Example world simulation
pred show []{
	#Badge=3
	#User=6
	#Participant=4
	#Team=4
	#Tournament=1
	#Battle=2
}
run show for 6
