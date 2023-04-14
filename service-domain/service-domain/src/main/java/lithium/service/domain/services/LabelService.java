package lithium.service.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.domain.data.entities.Label;
import lithium.service.domain.data.repositories.LabelRepository;

@Service
public class LabelService {
	
	@Autowired LabelRepository repository;

	public Label processCreateRequest(Label label) {
		repository.save(label);
		return label;
	}

	public Label findOrCreate(String name) {
		Label label = repository.findByName(name);
		if (label != null) return label;
		return processCreateRequest(Label.builder().name(name).build());
	}
}
