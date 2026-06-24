package org.example.util;

import org.example.controller.*;
import org.example.repository.FichaRepository;
import org.example.repository.PedidoRepo;
import org.example.repository.ProdutoRepo;
import org.example.repository.UsuarioRepository;
import org.example.service.*;

/**
 * Monta toda a árvore de dependências da aplicação em um único lugar.
 * Antes, cada tela (View) criava suas próprias instâncias de Repository e
 * Service — o que gerava acoplamento direto entre View e Service/Repository
 * e duplicação de código de inicialização em cada Frame.
 *
 * Agora a View pede um Controller já pronto a partir daqui, e o Controller
 * é o único ponto de contato entre View e as camadas de Service/Repository.
 */
public final class AppContext {

    private static final ProdutoRepo PRODUTO_REPO = new ProdutoRepo();
    private static final PedidoRepo PEDIDO_REPO = new PedidoRepo();
    private static final FichaRepository FICHA_REPOSITORY = new FichaRepository();
    private static final UsuarioRepository USUARIO_REPOSITORY = new UsuarioRepository();

    private static final EstoqueService ESTOQUE_SERVICE = new EstoqueService(PRODUTO_REPO);
    private static final PedidoService PEDIDO_SERVICE = new PedidoService(PEDIDO_REPO, ESTOQUE_SERVICE);
    private static final FichaService FICHA_SERVICE = new FichaService(FICHA_REPOSITORY, PEDIDO_SERVICE);
    private static final AuthService AUTH_SERVICE = new AuthService(USUARIO_REPOSITORY);
    private static final RelatorioService RELATORIO_SERVICE = new RelatorioService(PEDIDO_SERVICE);

    private AppContext() {
    }

    public static LoginController loginController() {
        return new LoginController(AUTH_SERVICE);
    }

    public static UsuarioController usuarioController() {
        return new UsuarioController(AUTH_SERVICE);
    }

    public static EstoqueController estoqueController() {
        return new EstoqueController(ESTOQUE_SERVICE);
    }

    public static PedidoController novoPedidoController() {
        return new PedidoController(PEDIDO_SERVICE, ESTOQUE_SERVICE, FICHA_SERVICE);
    }

    public static FichaController fichaController() {
        return new FichaController(FICHA_SERVICE);
    }

    public static RelatorioController relatorioController() {
        return new RelatorioController(RELATORIO_SERVICE);
    }
}
