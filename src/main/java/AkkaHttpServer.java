public class AkkaHttpServer {
    //создаем с помощью api route в акка http сервер который принимает два
    //параметра, и если счетчик не равен 0, то сначала получает новый урл сервера
    //(от актора хранилища конфигурации) и делает запрос к нему с аналогичными
    //query параметрами (url, counter) но счетчиком на 1 меньше. Либо осуществляет
    //запрос по url из параметра

    //a. Инициализация http сервера в akka
        System.out.println("start!");
    ActorSystem system = ActorSystem.create("routes");
    final AsyncHttpClient asyncHttpClient = asyncHttpClient();
    final Http http = Http.get(system);
    final ActorMaterializer materializer = ActorMaterializer.create(system);

    //тестировщик..
    final TestPerformer test = new TestPerformer(materializer, system, asyncHttpClient);

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = test.createFlow(); //<вызов
    //метода которому передаем Http, ActorSystem и ActorMaterializer>;
    final CompletionStage<ServerBinding> binding = http.bindAndHandle(
            routeFlow,
            ConnectHttp.toHost("localhost", 8086),
            materializer
    );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
}
