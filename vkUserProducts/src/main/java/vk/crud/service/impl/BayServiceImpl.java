package vk.crud.service.impl;

import org.springframework.stereotype.Service;
import vk.crud.client.payments.ClientBalanceRestClient;
import vk.crud.client.product.ProductRestClient;
import vk.crud.model.dto.ClientProductRequest;
import vk.crud.model.dto.ClientProductResponse;
import vk.crud.model.dto.UserDto;
import vk.crud.model.dto.mappers.DtoMapper;
import vk.crud.model.dto.payments.ClientBalanceDto;
import vk.crud.model.dto.prodact.ProductDto;
import vk.crud.service.BayService;
import vk.crud.service.ClientProductService;
import vk.crud.service.UserService;
import vk.crud.web.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;

@Service
public class BayServiceImpl implements BayService {

    private final ClientBalanceRestClient clientBalanceRestClient;
    private final ProductRestClient productRestClient;
    private final ClientProductService clientProductService;
    private final UserService userService;

    public BayServiceImpl(ClientBalanceRestClient clientBalanceRestClient,
                          ProductRestClient productRestClient,
                          ClientProductService clientProductService,
                          UserService userService) {
        this.clientBalanceRestClient = clientBalanceRestClient;
        this.productRestClient = productRestClient;
        this.clientProductService = clientProductService;
        this.userService = userService;
    }

    @Override
    public UserDto bay(UserDto request) {
        request.getProducts().forEach( p -> {
            ProductDto pdto = productRestClient.getProductById(p.getId());
            if (p.getQuantity() <= pdto.getStock()) {
                ClientBalanceDto clientBalanceDto = clientBalanceRestClient.getBalanceByClientId(request.getId().toString(), pdto.getId());
                if (clientBalanceDto.getBalance().compareTo(pdto.getPrice().multiply(BigDecimal.valueOf(p.getQuantity()))) > 0) {
                    //отсюда убираем
                    productRestClient.updateStock(p.getId(), pdto.getStock() - p.getQuantity());
                    //отсюда тоже
                    BigDecimal bd = clientBalanceDto.getBalance().subtract(pdto.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())));
                    clientBalanceRestClient.updateBalance(clientBalanceDto.getId(), request.getId().toString(),  "номер моей карты",
                            clientBalanceDto.getBalance().subtract(bd) );
                    //а сюда добавляем
                    ClientProductResponse clientProductResponse = clientProductService.getProductByIdAndUserId(p.getId(), request.getId());
                    if (clientProductResponse != null) {
                        clientProductResponse.setBalance(clientProductResponse.getBalance().subtract(BigDecimal.valueOf(p.getQuantity())));
                        ClientProductRequest clientProductRequest = DtoMapper.toClientProductRequest(clientProductResponse);
                        clientProductService.updateProduct(p.getId(), request.getId(), clientProductRequest);
                    } else {
                        ClientProductRequest clientProductRequest = new ClientProductRequest();
                        clientProductRequest.setBalance(BigDecimal.valueOf(p.getQuantity()));
                        clientProductService.createProduct(request.getId(), clientProductRequest);
                    }
                } else {
                    throw new ResourceNotFoundException("Нет денег!");
                }
            } else {
                throw new ResourceNotFoundException("Нет нужного количества!");
            }
        });
        return request;
    }
}
