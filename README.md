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



## 차가운 Observable, 뜨거운 Observable

+ Observable은 시간성을 담고 있다. 미래에 데이터가 들어오기를 기대하면서 Observable을 선언하고 그 동작을 처리할 Subscribe를 구성한다. 바로 들어올 수도 있지만, 언제 들어올지 모른다. 연속으로 들어 올 수도 있고, 텀을 두고 들어올 수 있다.

+ 이때, 데이터의 들어오기로 예상되는 시간을 기준으로 차갑고 뜨거운 Observable로 구분하는데, 

   느긋하게(Lazy) 내가 원할 때 시작하는 것을 차가운 Observable이라 한다. 대부분의 Observable은 차가운 Observable이다.

  반면, UI 이벤트, 주식 데이터, 센서 데이터 등 계속적으로 발행자가 원할 때 데이터를 보내는 것을 뜨거운 Observable이라 한다.

+ 위 예시를 두고 볼 때 앞서 했던 구구단은 차가운 Observable에 해당한다. 뜨거운 Observable은 이 차가운 Observable을 변환하여 만든다.

### [방법 1] <a herf=http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/observables/ConnectableObservable.html>ConnetableObservable 클래스</a>

![img](https://github.com/ReactiveX/RxJava/wiki/images/rx-operators/publishConnect.png)

1. Observable 클래스

   1. publish(): 일반 Observable을 ConnetableObservable로 변환시켜 준다.
   2. Subscribe(): 동작을 정의하지만 데이터가 나오지는 않는다.

2. ConnetableObservable 클래스

   1. Connet() : 여기서 데이터가 산출
   2. refCount() : 이 Obserable을 구독하는 구독자의 수를 알려준다.

3. <a herf=https://github.com/ReactiveX/RxJava/wiki/Connectable-Observable-Operators>예제</a> 분석

   1. 복수의 구독자가 있을 때 차가운 Observable은 같은 데이터를 처리 후 모두 같은 구독자에 같은 결과 값을 넘겨준다. 하지만 뜨거운 Observable의 경우 한번 제공된 데이터는 사라지고 지금 데이터를 참조하여 다시 연산을 하게 된다.

      <pre><code>

      ```
      def firstMillion  = Observable.range( 1, 1000000 ).sample(7, java.util.concurrent.TimeUnit.MILLISECONDS);

      firstMillion.subscribe(
         { println("Subscriber #1:" + it); },       // onNext
         { println("Error: " + it.getMessage()); }, // onError
         { println("Sequence #1 complete"); }       // onCompleted
      );

      firstMillion.subscribe(
          { println("Subscriber #2:" + it); },       // onNext
          { println("Error: " + it.getMessage()); }, // onError
          { println("Sequence #2 complete"); }       // onCompleted
      );
      ```

      </code></pre>

      * 결과

      <pre><code>

      ```
      Subscriber #1:211128
      Subscriber #1:411633
      Subscriber #1:629605
      Subscriber #1:841903
      Sequence #1 complete
      Subscriber #2:244776
      Subscriber #2:431416
      Subscriber #2:621647
      Subscriber #2:826996
      Sequence #2 complete
      ```

      </code></pre>

   2. 이렇게 여러 구독자를 가지고 있지만, 연속되는 데이터 스트림을 다룰 때 구독자들에게 동일한 결과물을 제공해야 할 필요가 있을 수 있다. 이 경우 구독자들을 대기시켜 놓을 수 있는데,

      이 때 사용하는 것이 connet() 이다.

      <pre><code>

      ```java
      def firstMillion  = Observable.range( 1, 1000000 ).sample(7, java.util.concurrent.TimeUnit.MILLISECONDS).publish();

      firstMillion.subscribe(
         { println("Subscriber #1:" + it); },       // onNext
         { println("Error: " + it.getMessage()); }, // onError
         { println("Sequence #1 complete"); }       // onCompleted
      );

      firstMillion.subscribe(
         { println("Subscriber #2:" + it); },       // onNext
         { println("Error: " + it.getMessage()); }, // onError
         { println("Sequence #2 complete"); }       // onCompleted
      );

      firstMillion.connect();
      ```

      </pre></code>

      * 결과

      <pre><code>

      ```
      Subscriber #2:208683
      Subscriber #1:208683
      Subscriber #2:432509
      Subscriber #1:432509
      Subscriber #2:644270
      Subscriber #1:644270
      Subscriber #2:887885
      Subscriber #1:887885
      Sequence #2 complete
      Sequence #1 complete
      ```

      </code></pre>



### [방법 2] <a herf=http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/subjects/PublishSubject.html>PublishSubject 클래스</a>

<pre><code>
PublishSubject<Object> subject = PublishSubject.creat();

//Observable1 will receuve all onNext and OnComplete envents
subject.subscribe.(Observable1);
subject.onNext("one");
subject.onNext("two");

//Observable2 will only receive "three" and onComplete
subject.subscribe(Observable2);
subject.onNext("three");
subject.onComplete();
</code></pre>

* 실시간으로 런타임이 발생하고 그 때 구독한 구독자에게만 데이터가 전달됩니다. 차가운 Observable의 경우 지금 구독하던 10초 후에 구독하던 똑같은 데이터를 받을 수 있었지만, 이 경우 그 때 그 때 onNext로 다음으로 넘겨버리기 때문에 현재 구독중이지 않은 구독자는 데이터를 받 을 수 없습니다.
* 이 형태는 그 순간 순간 다른 실시간 데이터를 전달하는 뜨거운 Observable의 형태와 동일합니다.



## 예외처리



1. 전통적인 JAVA에서의 예외처리의 경우는 Try/Catch, finally 구문을 사용하고 C++,C# 등등 다양한 언어에서 이러한 예외 처리 방법들을 사용중이다.

2. 간단한 예외처리의 경우 RxJava에서는 Subscribe에서 처리 가능합니다.

   <pre><code>

   ```
   .subscribe(
   	textView1::append,
   	e-> Toast.makeText(this,"Only number",Toast.LENGTH_LONG).show()
   );
   ```

   </code></pre>

3. RxJava에서는 try/catch 문의 사용을 권장하지 않습니다. 데이터의 연속되는 흐름에서 onError로 보내는 경우는 치명적인 손상으로 복구 할 수 없을 경우에 사용 하도록 하고 있습니다.

   공식 문서에서는 이벤트 흐름을 정상 흐름으로 복구하도록 권장 합니다.

   (https://github.com/ReactiveX/RxJava/wiki/Error-Handling-Operators)

4. 정상 흐름으로 복구 하기 위한 4가지 방법으로

   1. swallow the error and switch over to a backup Observable to continue the sequence (백업 Observable로 교체)
   2. swallow the error and emit a default item (기본 값을 발행)
   3. swallow the error and immediately try to restart the failed Observable (즉시 실패한 Observable을 다시 실행)
   4. swallow the error and try to restart the failed Observable after some back-off interval (일정 시간을 기다렸다가 실패한 Observable을 재시작)

5. subscribe에서 처리하지 않고 이와 같이 전략을 취하는 이유는 Observable을 생성할 때 flatMap,zip,filter 등 수 많은 함수를 이용해서 처리하게 되는데 가장 말단 흐름에 있는 Subscribe에서 처리하는 OnError하나로는 다 감당 할 수 없기 때문입니다.

6. 따라서 이런 에러들을 처리 할 수 있는 다양한 Operator들을 사용해서 처리 하도록 하고 있습니다.

   onError, onErrorReturn, onErrorResumeNext, retry, retryWhen 등이 있습니다.

   http://pluu.github.io/blog/rxjava/2017/02/26/rx-error/ 포스트 참조