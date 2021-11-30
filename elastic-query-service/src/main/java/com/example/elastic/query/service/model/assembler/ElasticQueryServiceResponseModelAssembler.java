package com.example.elastic.query.service.model.assembler;

import com.example.elastic.model.impl.TelegramIndexModel;
import com.example.elastic.query.service.api.ElasticDocumentController;
import com.example.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.example.elastic.query.service.transformer.ElasticResponseModelTransformer;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ElasticQueryServiceResponseModelAssembler
        extends RepresentationModelAssemblerSupport<TelegramIndexModel, ElasticQueryServiceResponseModel> {

    private final ElasticResponseModelTransformer transformer;

    public ElasticQueryServiceResponseModelAssembler(ElasticResponseModelTransformer transformer) {
        super(TelegramIndexModel.class, ElasticQueryServiceResponseModel.class);
        this.transformer = transformer;
    }

    @Override
    public ElasticQueryServiceResponseModel toModel(TelegramIndexModel telegramIndexModel) {
        ElasticQueryServiceResponseModel elasticQueryModel = transformer.transform(telegramIndexModel);
        elasticQueryModel.add(
                linkTo(methodOn(ElasticDocumentController.class).getDocumentById(telegramIndexModel.getId())).withSelfRel()
        );
        elasticQueryModel.add(
                linkTo(ElasticDocumentController.class).withRel("documents")
        );
        return elasticQueryModel;
    }
}

