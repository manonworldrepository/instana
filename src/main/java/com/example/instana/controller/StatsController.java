package com.example.instana.controller;

import com.example.instana.service.GraphCalculationService;
import com.example.instana.service.LineReader;
import com.example.instana.service.StringFormatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private LineReader reader;

    @Autowired
    private GraphCalculationService graphCalculationService;

    @Autowired
    private StringFormatterService stringFormatterService;

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseStatus(OK)
    public Flux<String> index(@RequestPart("file") Flux<FilePart> file) {
        return reader.read(file)
            .flatMap(graph ->
                graphCalculationService.prepareCalculationsWithIds(graph)
                    .collectList()
                    .map(stringFormatterService::formatAllResults)
            )
            .concatWith(Flux.just(""));
    }
}
