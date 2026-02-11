package com.social.Repository;

import com.social.Model.Conversation;
import com.social.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository
extends JpaRepository<Conversation, Long> {

	@Query("""
		    SELECT cp.conversation
		    FROM ConversationParticipant cp
		    WHERE cp.user = :u1
		      AND cp.deleted = false
		      AND cp.conversation.group = false
		      AND EXISTS (
		          SELECT 1
		          FROM ConversationParticipant cp2
		          WHERE cp2.conversation = cp.conversation
		            AND cp2.user = :u2
		            AND cp2.deleted = false
		      )
		""")
		Optional<Conversation> findDirectConversationBetweenUsers(
		    @Param("u1") User u1,
		    @Param("u2") User u2
		);


//@Query("""
//SELECT DISTINCT cp.conversation
//FROM ConversationParticipant cp
//WHERE cp.user = :user
//  AND cp.deleted = false
//""")
//List<Conversation> findUserConversations(
//@Param("user") User user
//);
	
//	@Query("""
//			SELECT DISTINCT c
//			FROM Conversation c
//			JOIN FETCH c.participants p
//			WHERE c.id IN (
//			    SELECT cp.conversation.id
//			    FROM ConversationParticipant cp
//			    WHERE cp.user = :user AND cp.deleted = false
//			)
//			AND p.deleted = false
//			""")
//			List<Conversation> findUserConversations(@Param("user") User user);

	@Query("""
			SELECT DISTINCT cp.conversation
			FROM ConversationParticipant cp
			WHERE cp.user.id = :userId
			  AND cp.deleted = false
			""")
			List<Conversation> findUserConversations(@Param("userId") Long userId);

	
@Query("""
SELECT c
FROM Conversation c
LEFT JOIN FETCH c.participants p
LEFT JOIN FETCH p.user
WHERE c.id = :id
""")
Optional<Conversation> findByIdWithParticipants(
@Param("id") Long id
);
}
