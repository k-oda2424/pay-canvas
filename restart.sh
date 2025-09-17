#!/bin/bash

# PayCanvas 再起動スクリプト
# 使用方法:
#   ./restart.sh    - フロントエンド・バックエンド両方を再起動
#   ./restart.sh f  - フロントエンドのみ再起動
#   ./restart.sh b  - バックエンドのみ再起動

# スクリプトのディレクトリに移動
cd "$(dirname "$0")"

# 色付きメッセージ用の関数
print_info() {
    echo -e "\033[34m[INFO]\033[0m $1"
}

print_success() {
    echo -e "\033[32m[SUCCESS]\033[0m $1"
}

print_error() {
    echo -e "\033[31m[ERROR]\033[0m $1"
}

# バックエンド停止
stop_backend() {
    print_info "バックエンドを停止中..."

    # Gradleプロセスを停止
    pkill -f "gradle.*bootRun" 2>/dev/null || true
    pkill -f "java.*paycanvas" 2>/dev/null || true

    # ポート8080を使用しているプロセスを停止
    if lsof -ti:8080 >/dev/null 2>&1; then
        lsof -ti:8080 | xargs kill -9 2>/dev/null || true
    fi

    sleep 2
    print_success "バックエンド停止完了"
}

# フロントエンド停止
stop_frontend() {
    print_info "フロントエンドを停止中..."

    # npm run devプロセスを停止
    pkill -f "npm.*run.*dev" 2>/dev/null || true
    pkill -f "vite" 2>/dev/null || true

    # ポート5173を使用しているプロセスを停止
    if lsof -ti:5173 >/dev/null 2>&1; then
        lsof -ti:5173 | xargs kill -9 2>/dev/null || true
    fi

    sleep 2
    print_success "フロントエンド停止完了"
}

# バックエンド起動
start_backend() {
    print_info "バックエンドを起動中..."
    cd src/backend
    ./gradlew bootRun > /dev/null 2>&1 &
    cd ../..

    # 起動確認（最大30秒待機）
    for i in {1..30}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "バックエンド起動完了 (http://localhost:8080)"
            return 0
        fi
        sleep 1
    done

    print_error "バックエンドの起動に失敗しました"
    return 1
}

# フロントエンド起動
start_frontend() {
    print_info "フロントエンドを起動中..."
    npm run dev > /dev/null 2>&1 &

    # 起動確認（最大30秒待機）
    for i in {1..30}; do
        if curl -s -o /dev/null -w "%{http_code}" http://localhost:5173 | grep -q "200"; then
            print_success "フロントエンド起動完了 (http://localhost:5173)"
            return 0
        fi
        sleep 1
    done

    print_error "フロントエンドの起動に失敗しました"
    return 1
}

# メイン処理
case "${1:-both}" in
    "f"|"frontend")
        print_info "=== フロントエンド再起動 ==="
        stop_frontend
        start_frontend
        ;;
    "b"|"backend")
        print_info "=== バックエンド再起動 ==="
        stop_backend
        start_backend
        ;;
    "both"|"")
        print_info "=== フロントエンド・バックエンド再起動 ==="
        stop_frontend
        stop_backend
        start_backend
        start_frontend
        ;;
    *)
        print_error "無効な引数です"
        echo "使用方法:"
        echo "  $0      - フロントエンド・バックエンド両方を再起動"
        echo "  $0 f    - フロントエンドのみ再起動"
        echo "  $0 b    - バックエンドのみ再起動"
        exit 1
        ;;
esac

print_success "再起動処理完了"