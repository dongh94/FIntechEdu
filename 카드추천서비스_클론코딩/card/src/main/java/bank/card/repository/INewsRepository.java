package bank.card.repository;

import bank.card.dto.NewsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface INewsRepository {
    public List<NewsEntity> newsEntities();
}
