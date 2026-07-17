package Telecom.SubscriptionService.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import Telecom.SubscriptionService.dto.PlanDto;
import Telecom.SubscriptionService.model.Plan;
import Telecom.SubscriptionService.repository.PlanRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Plan getPlanById(Long id) {
        return planRepository.findById(id).orElse(null);
    }

    public Plan createPlan(PlanDto dto) {
        Plan plan = new Plan();
        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setDetails(dto.getDetails());
        return planRepository.save(plan);
    }

    public Plan updatePlan(Long id, PlanDto dto) {
        return planRepository.findById(id).map(plan -> {
            plan.setName(dto.getName());
            plan.setPrice(dto.getPrice());
            plan.setDetails(dto.getDetails());
            return planRepository.save(plan);
        }).orElse(null);
    }

    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }
}
