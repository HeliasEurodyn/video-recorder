package com.ed.video_recorder.repository;

import com.ed.video_recorder.model.RecordedStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface RecordStreamRepository extends JpaRepository<RecordedStream, Long> {

    @Transactional
    @Modifying
    void deleteByFileNameEqualsAndStreamIdEquals(String filename, Long id);


}
