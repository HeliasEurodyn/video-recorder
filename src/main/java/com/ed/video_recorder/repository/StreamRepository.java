package com.ed.video_recorder.repository;


import com.ed.video_recorder.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamRepository extends JpaRepository<Stream,Long> {

    List<Stream> findByStatus(String status);
}
