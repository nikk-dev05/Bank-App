package in.sp.main.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.sp.main.entity.User1;

@Repository
public interface User_Inforepo extends JpaRepository<User1, Integer> {
	Optional<User1> findByUsername(String username);
	 boolean existsByUsername(String username);

}
