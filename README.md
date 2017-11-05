# RxJavaTesting

본 페이지는https://academy.realm.io/kr/posts/mobilization-hugo-visser-rxjava-for-rest-of-us/를 학습하며 작성하였습니다. (android에서 RxJava 적용하기.)

(http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/ - RxJava 기초)



## 필요성

1. RxJava는 반응형 프로그래밍으로 데이터의 흐름에 따라 프로그래밍한다.

   * 기존의 java 프로그래밍은 명령형에 가깝다.
   * 과거 프로젝트에서 비동기 통신의 병렬 처리에서 불편함을 느낀 경험이 있는데, 이에 좀 더 유용하다고 추천받아 RxJava를 학습하려 한다.
   * 비동기 통신의 병렬 처리에 대해서는 나 말고도 시연에서 많은 사람들이 트러블 슈팅으로 다룬 내용이기 때문에 RxJava의 필요성은 높다고 볼 수 있다.
   * 반응형의 기본적인 틀은 [*Observable*은 아이템들을 발행(emit)하고, *Subscriber*는 이 아이템들을 소비한다(consume).] 이다. 이는 <a herf=https://ko.wikipedia.org/wiki/%EC%98%B5%EC%84%9C%EB%B2%84_%ED%8C%A8%ED%84%B4>옵저버 패턴</a>과 유사하며 발행/구독 모델로 알려져 있다.<br>
2. RxJava와 함께 공부해야할 것들

   * RxJava에서는 java8에서 추가된 lamda express를 주로 사용하고 있다. 필수는 아닌 것 같지만 대부분의 RxJava 예제에서 lamda express를 사용하고 있으니 학습이 필요하다. 
     * 과거 프로그래밍의 구조와 이해에서 스킴과 함께 Lamda express에 대해서 배웠기 때문에 그리 어려워 보이지는 않는다.
   * 옵저버 패턴에 대해서 알아야 한다. 데이터 바인딩에서도 주로 나왔듯이 옵저버 패턴은 몹시 중요해 보인다. 객체에 옵저버와 섭스크라이브(구독자)를 만들어 데이터의 변화에 맞춰 자동으로 에러 처리 등을 정의해 놓는 방식으로 보인다.<br>

## Lamda express의 예시

1. 가장 쉬운 Rx(https://brunch.co.kr/@yudong/34 를 참고하였습니다.)

   1. gradle 설정 ( https://github.com/ReactiveX/RxAndroid ) 여기서 최신 버전 확인

   2. lamda식 설정

      <pre><code>

      ```
      defaultConfig {
        jackOptions{
        	enabled true
        }
      }
      compileOptions{
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
      }
      ```

      </code></pre>

      * 특이 사항으로 jackOptions은 설정하고 하면 워밍이 발생하는데, 읽어보면 Java8에서는 jackOptions를 넣으면 동작하지 않는다는 경고문이다. 넣고하든 빼고 하든 동작은 되는데, 워밍을 없애기 위해 빼고 한다.

        <pre><code>

        ```
        Warning:The Jack toolchain is deprecated and will not run. To enable support for Java 8 language features built into the plugin, remove 'jackOptions { ... }' from your build.gradle file, and add

        android.compileOptions.sourceCompatibility 1.8
        android.compileOptions.targetCompatibility 1.8

        Future versions of the plugin will not support usage of 'jackOptions' in build.gradle.
        To learn more, go to https://d.android.com/r/tools/java-8-support-message.html
        ```

        </code></pre>

   3. RxJava를 lamda식으로 코딩

      <pre><code>

      ```
      //세상에서 가장 간단한 Rx
          TextView textView = (TextView)findViewById(R.id.txtHello);
          Observable.just(textView.getText().toString())
                  .map(s -> s+ " Rx!")
                  .subscribe(text->textView.setText(text));
      }
      ```

      </code></pre>

      위 코드는 map()과 subscribe() 두 군데에서 람다식이 사용되었는데, 다양한 lamda식 활용은 다음 링크에서 확인 가능하다.

      http://rxmarbles.com/ - > Rxlamda식을 그림과 함께 표현

      http://reactivex.io/documentation/operators.html

      실행하면 TextView에 있던 text를 읽어와 Rx!를 뒤에 붙여, 다시 TextView에 표시한다.

      * 크게보면 RxJava의 Lamda식은 3가지 부분으로 구성된다.
        1. input : 이벤트가 시작되는 부분 (just)
        2. operator : 이벤트를 가공하고 조합하여 결과를 만드는 부분
        3. output : 가공한 결과를 출력하는 부분<br>

2. RxJava에서의 반복문 처리

   1. 기존 명령형에서의 반복문 처리

   ```
   int den =3;

   for(int i=1;i<=9;i++){
   	System.out.println(den + "*"+i+"="+(den*i));
   }
   ```

   2. 반응형에서의 반복문 처리

   ```
   Observable.range(1,9)
     .map(row->den + "*"+row+"="+(den*row)+"\n")
     .subscribe(textView1::append);
   ```

   - `textView1::append`에서 :: 표현은 Java 8 부터 추가된 람다식 표현법으로 인자를 생략 할 수 있다. 원래 코드는 `subscribe(text->textView1.append(text));` 이다<br>

## 구구단을 UI와 연결하기 (버튼에 이벤트 연결)

​	1. 하드코딩하지 않고 입력 받은 숫자로 구구단을 출력하도록 한다.

```
btnMulti.setOnClickListener(view -> {
  int den = Integer.parseInt(inputText.getText().toString());

  textView1.setText("");

  Observable.range(1,9)
    .map(row->den + "*"+row+"="+(den*row)+"\n")
    .subscribe(textView1::append);
});
```

   	2.  예외처리 추가하기

```
btnMulti.setOnClickListener(view -> {
  int den = Integer.parseInt(inputText.getText().toString());

  textView1.setText("");

  Observable.range(1,9)
    .map(row->den + "*"+row+"="+(den*row)+"\n")
    .subscribe(
      textView1::append,
      e-> Toast.makeText(this,"Only number",Toast.LENGTH_LONG).show()
      );
});
```

- subscribe는 onNext, onError, onComplete의 3가지 결과 처리를 지원하고 있다. Observable 부분에서 에러가 발생한다면 e-> 부분에서 처리 가능한데, 이 경우 입력 값을 잘못 넣으면 `int den = Integer.parseInt(inputText.getText().toString());` 부분에서 에러가 발생한다.
- eidtText에서 직접 inputType을 지정하는 방법도 있고 사전에 예외 처리하는 방법도 있고 다양한 방법이 있지만, RxJava와 상관 없기 때문에 넘어가도록 한다.

### flatMap의 활용

```
btnMulti.setOnClickListener(view -> {
  textView1.setText("");
  Observable.just(inputText.getText().toString())
    .map(den->Integer.parseInt(den))
    .filter(den -> den>1&&den<10)
    .flatMap(
      den -> Observable.range(1,9),
      (den, row) -> den+" * "+row+" = "+(den*row)+"\n")
    .subscribe(
      textView1::append,
      e-> Toast.makeText(this,"Only number",Toast.LENGTH_LONG).show());
});
```

* 위 코드를 잘 보면 flatMap이라는 문법이 추가되었는데, 이는 기존에 있던 den 값을 계속 변경하면서 처리하던 중이라도 처음에 인자로 넘겨온 den 값을 사용 할 수 있도록 되어있다. 

  (http://reactivex.io/documentation/operators/flatmap.html 참고)

* 예를 들어서 den 값은 처음에는 int의 숫자였지만 `(den, row) -> den+" * "+row+" = "+(den*row)+"\n")` 을 반복하는 동안 결과 값의 텍스트로 전환된다. 하지만 `(den, row) -> den+" * "+row+" = "+(den*row)+"\n")` 이 반복적으로 호출되는 동안에 처음 값의 den이 계속 필요한데 flatMap으로 해 놓으면 안에 데이터를 구할 수 있다. 이 같은 끼어들기(interleaving)을 허용 한다.

* 끼어들기를 방지하는 concatMap도 있다고 한다.

