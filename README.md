# baekss.github.io
This study provides an approach to apply AOP to event handler function of elements.

この見本はelementのevent handler functionにAOPを適用したことでproxyパターンが活用されました。

해당 코드는 요소의 이벤트 핸들러 함수에 프록시 패턴을 이용하여 AOP를 적용하였습니다.

## tutorial.aop.js
- eventProperties is indicated each trigger event (e.g. click, change, keyup, ..., etc.). We can use this object to prevent miss typing when we pass arguments to AopScan's constructor.
- ElementScan has a constructor for target elements to apply AOP. We just pass the element array object when ElementScan function is constructed as an object with new keyword.
- AopScan has a constructor to inject advice function. We just pass the ElementScan, trigger event property array object and advice functions that is worked as additional function at before or after from actual handler function when AopScan function is constructed as an object with new keyword.

- eventPropertiesはclick, change, keyupなどのtrigger eventを表します。AopScanのconstructorに渡すparameterを作るとき間違い止めとして活用できます。
- ElementScanのconstructorにはAOPを適用したいelementたちをarray objectにして渡します。
- AopScanのconstructorにはElementScan objectとarray objectにしたtrigger eventたち、あと、extra作業を担当してるfunctionたちを渡します。

- eventProperties는 click, change, keyup과 같은 트리거 이벤트를 나타내며 AopScan의 생성자에 넘길 인자값 작성시 오타 방지를 위해 활용될 수 있습니다.
- ElementScan의 생성자에는 AOP를 적용할 요소의 배열을 넘겨줍니다.
- AopScan의 생성자에는 ElementScan객체와 트리거 이벤트 그리고 부가적 관점 로직을 수행할 함수들을 넘겨줍니다.

### Wiki 에서는 다양한 상황에 대비한 접근법을 제공합니다.
https://github.com/baekss/baekss.github.io/wiki
