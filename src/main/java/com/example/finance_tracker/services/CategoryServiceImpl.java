package com.example.finance_tracker.services;

import com.example.finance_tracker.entities.CategoryEntity;
import com.example.finance_tracker.entities.UserEntity;
import com.example.finance_tracker.exceptions.ResourceNotFoundException;
import com.example.finance_tracker.exceptions.ValidationException;
import com.example.finance_tracker.mappers.CategoryMapper;
import com.example.finance_tracker.models.Category;
import com.example.finance_tracker.repositories.CategoryRepository;
import com.example.finance_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Category create(Category category) {
        validateName(category.getName());

        UserEntity userEntity = findUserInDb(category.getUserId());

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(category.getName());
        categoryEntity.setType(category.getType());
        categoryEntity.setDescription(category.getDescription());
        categoryEntity.setUserEntity(userEntity);

        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);
        return categoryMapper.toModel(savedCategory);
    }

    @Override
    @Transactional
    public Category update(Category category) {
        CategoryEntity categoryEntity = findCategoryInDb(category.getId(), category.getUserId());

        validateName(category.getName());

        categoryEntity.setName(category.getName());
        categoryEntity.setType(category.getType());
        categoryEntity.setDescription(category.getDescription());

        return categoryMapper.toModel(categoryEntity);
    }

    @Override
    @Transactional
    public void delete(Long categoryId, Long userId) {
        if (categoryRepository.existsByIdAndUserEntityId(categoryId, userId)) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new ResourceNotFoundException(
                    "Category with id " + categoryId + " was not found for user with id " + userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getByUser(Long userId) {
        return categoryRepository.findByUserEntityId(userId).stream()
                .map(categoryMapper::toModel).toList();
    }

    private UserEntity findUserInDb(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " was not found"));
    }

    private CategoryEntity findCategoryInDb(Long categoryId, Long userId) {
        return categoryRepository.findByIdAndUserEntityId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category with id " + categoryId + " was not found for user with id " + userId));
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name of category cannot be null or empty");
        }
    }
}
